package com.mit.notepad.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.mit.notepad.R;
import com.mit.notepad.activities.MainActivity;
import com.mit.notepad.activities.NoteViewActivity;
import com.mit.notepad.activities.TodoEditorActivity;
import com.mit.notepad.utils.Constants;

/** Creates notification channels and posts system notifications. */
public final class NotificationHelper {
    private NotificationHelper() { }

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = context.getSystemService(NotificationManager.class);
        if (nm == null) return;
        NotificationChannel reminders = new NotificationChannel(Constants.CHANNEL_REMINDERS,
                context.getString(R.string.channel_reminders), NotificationManager.IMPORTANCE_HIGH);
        reminders.setDescription(context.getString(R.string.channel_reminders_desc));
        NotificationChannel alarms = new NotificationChannel(Constants.CHANNEL_ALARMS,
                context.getString(R.string.channel_alarms), NotificationManager.IMPORTANCE_HIGH);
        alarms.setDescription(context.getString(R.string.channel_alarms_desc));
        nm.createNotificationChannel(reminders);
        nm.createNotificationChannel(alarms);
    }

    /** True if the app may currently post notifications (permission + system switch). */
    public static boolean canPost(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * Posts a system notification. Silently does nothing when notifications are blocked; the
     * in-app history entry has already been stored by the caller.
     */
    public static void show(Context context, int notificationId, String category, String title,
                            String text, long noteId, long todoId, long historyId) {
        if (!canPost(context)) return;

        Intent target;
        if (noteId > 0) {
            target = new Intent(context, NoteViewActivity.class).putExtra(Constants.EXTRA_NOTE_ID, noteId);
        } else if (todoId > 0) {
            target = new Intent(context, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, todoId);
        } else {
            target = new Intent(context, MainActivity.class);
        }
        target.putExtra(Constants.EXTRA_NOTIFICATION_ID, historyId);

        TaskStackBuilder stack = TaskStackBuilder.create(context);
        stack.addNextIntentWithParentStack(target);
        PendingIntent contentIntent = stack.getPendingIntent(notificationId,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String channel = Constants.CATEGORY_ALARM.equals(category)
                ? Constants.CHANNEL_ALARMS : Constants.CHANNEL_REMINDERS;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Constants.CATEGORY_ALARM.equals(category)
                        ? NotificationCompat.CATEGORY_ALARM : NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
        } catch (SecurityException ignored) {
            // Permission revoked at the last moment; history entry already exists.
        }
    }
}
