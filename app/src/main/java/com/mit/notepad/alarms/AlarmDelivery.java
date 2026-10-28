package com.mit.notepad.alarms;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.mit.notepad.R;
import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Todo;
import com.mit.notepad.notifications.NotificationHelper;
import com.mit.notepad.repositories.AlarmRepository;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.NotificationRepository;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.Repeat;

/** What happens when an alarm fires: validate, re-arm/disable, store history, post notification. */
public final class AlarmDelivery {
    private AlarmDelivery() { }

    public static void deliver(Context context, long alarmId) {
        NotepadDatabase helper = NotepadDatabase.getInstance(context);
        AlarmRepository alarms = new AlarmRepository(context, helper);
        Alarm alarm = alarms.get(alarmId);
        if (alarm == null || !alarm.enabled) return; // cancelled/deleted in the meantime

        String title;
        String message;
        String category;
        if (alarm.todoId > 0) {
            Todo task = new TodoRepository(context, helper).get(alarm.todoId);
            if (task == null || task.completed) {
                alarms.disable(alarmId);
                return;
            }
            title = context.getString(R.string.notif_task_reminder);
            message = task.title;
            category = Constants.CATEGORY_TODO;
        } else {
            Note note = new NoteRepository(context, helper).get(alarm.noteId);
            if (note == null) return;
            Todo linked = note.isTodo() ? new TodoRepository(context, helper).getByNote(note.id) : null;
            if (linked != null && linked.completed) {
                alarms.disable(alarmId);
                return;
            }
            boolean task = linked != null;
            title = context.getString(task ? R.string.notif_task_reminder : R.string.notif_alarm);
            message = context.getString(R.string.notif_note_prefix, note.title);
            category = task ? Constants.CATEGORY_TODO : Constants.CATEGORY_ALARM;
        }

        // Move a repeating alarm forward (or switch a one-off alarm off) before notifying.
        long next = Repeat.nextOccurrence(alarm.scheduledAt, alarm.repeatType, alarm.repeatInterval,
                System.currentTimeMillis());
        if (next > 0) alarms.reschedule(alarmId, next);
        else alarms.disable(alarmId);

        SettingsRepository settings = new SettingsRepository(helper);
        boolean allowed = settings.getBool(Constants.SETTING_NOTIF_ENABLED, true)
                && settings.getBool(Constants.CATEGORY_TODO.equals(category)
                ? Constants.SETTING_NOTIF_TODO : Constants.SETTING_NOTIF_ALARM, true);

        long historyId = new NotificationRepository(helper).add(title, message, category,
                alarm.noteId, alarm.todoId);
        Intent ringing = new Intent(context, AlarmRingingService.class)
                .setAction(AlarmRingingService.ACTION_START)
                .putExtra(AlarmRingingService.EXTRA_TITLE, title)
                .putExtra(AlarmRingingService.EXTRA_MESSAGE, message);
        try {
            ContextCompat.startForegroundService(context, ringing);
        } catch (RuntimeException ignored) {
            // Keep the normal notification/history fallback if the OS blocks service startup.
        }
        if (allowed) {
            NotificationHelper.show(context, (int) historyId, category, title, message,
                    alarm.noteId, alarm.todoId, historyId);
        }
    }
}
