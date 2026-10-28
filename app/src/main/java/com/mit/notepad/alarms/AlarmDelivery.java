// Sets the package name to com.mit.notepad.alarms so this class belongs to the app module namespace.
package com.mit.notepad.alarms;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;

// Imports androidx.core.content.ContextCompat so this file can use that type by its short name.
import androidx.core.content.ContextCompat;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.notifications.NotificationHelper so this file can use that type by its short name.
import com.mit.notepad.notifications.NotificationHelper;
// Imports com.mit.notepad.repositories.AlarmRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.AlarmRepository;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.NotificationRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NotificationRepository;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.repositories.TodoRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TodoRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.Repeat so this file can use that type by its short name.
import com.mit.notepad.utils.Repeat;

/** What happens when an alarm fires: validate, re-arm/disable, store history, post notification. */
// Declares class AlarmDelivery to group related state and behavior.
public final class AlarmDelivery {
    // Declares AlarmDelivery() to implement this part of the component.
    private AlarmDelivery() { }

    // Records and displays the alarm, then advances or disables its schedule.
    public static void deliver(Context context, long alarmId) {
        // Initializes helper with NotepadDatabase.getInstance(context) so later statements can use the computed value.
        NotepadDatabase helper = NotepadDatabase.getInstance(context);
        // Initializes alarms with new AlarmRepository(context, helper) so later statements can use the computed value.
        AlarmRepository alarms = new AlarmRepository(context, helper);
        // Initializes alarm with alarms.get(alarmId) so later statements can use the computed value.
        Alarm alarm = alarms.get(alarmId);
        // If alarm == null || !alarm.enabled is true, return; // cancelled/deleted in the meantime immediately.
        if (alarm == null || !alarm.enabled) return; // cancelled/deleted in the meantime

        // Declares title for use by the surrounding method or class.
        String title;
        // Declares message for use by the surrounding method or class.
        String message;
        // Declares category for use by the surrounding method or class.
        String category;
        // If alarm.todoId > 0 is true, { immediately.
        if (alarm.todoId > 0) {
            // Initializes task with new TodoRepository(context, helper).get(alarm.todoId) so later statements can use the computed value.
            Todo task = new TodoRepository(context, helper).get(alarm.todoId);
            // If task == null || task.completed is true, { immediately.
            if (task == null || task.completed) {
                // Calls alarms.disable with alarmId to perform the operation shown.
                alarms.disable(alarmId);
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
            // Stores the result of context.getString(R.string.notif_task_reminder) in title for use by later logic.
            title = context.getString(R.string.notif_task_reminder);
            // Stores the result of task.title in message for use by later logic.
            message = task.title;
            // Stores the result of Constants.CATEGORY_TODO in category for use by later logic.
            category = Constants.CATEGORY_TODO;
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Initializes note with new NoteRepository(context, helper).get(alarm.noteId) so later statements can use the computed value.
            Note note = new NoteRepository(context, helper).get(alarm.noteId);
            // If note == null is true, return immediately.
            if (note == null) return;
            // Initializes linked with note.isTodo() ? new TodoRepository(context, helper).getByNote(note.id) : null so later statements can use the computed value.
            Todo linked = note.isTodo() ? new TodoRepository(context, helper).getByNote(note.id) : null;
            // If linked != null && linked.completed is true, { immediately.
            if (linked != null && linked.completed) {
                // Calls alarms.disable with alarmId to perform the operation shown.
                alarms.disable(alarmId);
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
            // Initializes task with linked != null so later statements can use the computed value.
            boolean task = linked != null;
            // Stores the result of context.getString(task ? R.string.notif_task_reminder : R.string.notif_alarm) in title for use by later logic.
            title = context.getString(task ? R.string.notif_task_reminder : R.string.notif_alarm);
            // Stores the result of context.getString(R.string.notif_note_prefix, note.title) in message for use by later logic.
            message = context.getString(R.string.notif_note_prefix, note.title);
            // Stores the result of task ? Constants.CATEGORY_TODO : Constants.CATEGORY_ALARM in category for use by later logic.
            category = task ? Constants.CATEGORY_TODO : Constants.CATEGORY_ALARM;
        // Closes the current method, control-flow, or type block.
        }

        // Move a repeating alarm forward (or switch a one-off alarm off) before notifying.
        // Initializes next with Repeat.nextOccurrence(alarm.scheduledAt, alarm.repeatType, alarm.repeatInterval, so later statements can use the computed value.
        long next = Repeat.nextOccurrence(alarm.scheduledAt, alarm.repeatType, alarm.repeatInterval,
                // Calls System.currentTimeMillis with  to perform the operation shown.
                System.currentTimeMillis());
        // If next > 0 is true, alarms.reschedule(alarmId, next) immediately.
        if (next > 0) alarms.reschedule(alarmId, next);
        // Runs the fallback branch when the preceding condition did not match.
        else alarms.disable(alarmId);

        // Initializes settings with new SettingsRepository(helper) so later statements can use the computed value.
        SettingsRepository settings = new SettingsRepository(helper);
        // Initializes allowed with settings.getBool(Constants.SETTING_NOTIF_ENABLED, true) so later statements can use the computed value.
        boolean allowed = settings.getBool(Constants.SETTING_NOTIF_ENABLED, true)
                // Requires the additional condition settings.getBool(Constants.CATEGORY_TODO.equals(category) to be true as well.
                && settings.getBool(Constants.CATEGORY_TODO.equals(category)
                // Executes ? Constants.SETTING_NOTIF_TODO : Constants.SETTING_NOTIF_ALARM, true) as a statement.
                ? Constants.SETTING_NOTIF_TODO : Constants.SETTING_NOTIF_ALARM, true);

        // Initializes historyId with new NotificationRepository(helper).add(title, message, category, so later statements can use the computed value.
        long historyId = new NotificationRepository(helper).add(title, message, category,
                // Executes alarm.noteId, alarm.todoId) as a statement.
                alarm.noteId, alarm.todoId);
        // Initializes ringing with new Intent(context, AlarmRingingService.class) so later statements can use the computed value.
        Intent ringing = new Intent(context, AlarmRingingService.class)
                // Calls setAction on the object from the preceding line with the shown arguments.
                .setAction(AlarmRingingService.ACTION_START)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(AlarmRingingService.EXTRA_TITLE, title)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(AlarmRingingService.EXTRA_MESSAGE, message);
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls ContextCompat.startForegroundService with context, ringing to perform the operation shown.
            ContextCompat.startForegroundService(context, ringing);
        // Calls catch with RuntimeException ignored) { to perform the operation shown.
        } catch (RuntimeException ignored) {
            // Keep the normal notification/history fallback if the OS blocks service startup.
        // Closes the current method, control-flow, or type block.
        }
        // If allowed is true, { immediately.
        if (allowed) {
            // Calls NotificationHelper.show with context, (int) historyId, category, title, message, to perform the operation shown.
            NotificationHelper.show(context, (int) historyId, category, title, message,
                    // Executes alarm.noteId, alarm.todoId, historyId) as a statement.
                    alarm.noteId, alarm.todoId, historyId);
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
