// Sets the package name to com.mit.notepad.notifications so this class belongs to the app module namespace.
package com.mit.notepad.notifications;

// Imports android.Manifest so this file can use that type by its short name.
import android.Manifest;
// Imports android.app.NotificationChannel so this file can use that type by its short name.
import android.app.NotificationChannel;
// Imports android.app.NotificationManager so this file can use that type by its short name.
import android.app.NotificationManager;
// Imports android.app.PendingIntent so this file can use that type by its short name.
import android.app.PendingIntent;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.content.pm.PackageManager so this file can use that type by its short name.
import android.content.pm.PackageManager;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;

// Imports androidx.core.app.NotificationCompat so this file can use that type by its short name.
import androidx.core.app.NotificationCompat;
// Imports androidx.core.app.NotificationManagerCompat so this file can use that type by its short name.
import androidx.core.app.NotificationManagerCompat;
// Imports androidx.core.app.TaskStackBuilder so this file can use that type by its short name.
import androidx.core.app.TaskStackBuilder;
// Imports androidx.core.content.ContextCompat so this file can use that type by its short name.
import androidx.core.content.ContextCompat;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.activities.MainActivity so this file can use that type by its short name.
import com.mit.notepad.activities.MainActivity;
// Imports com.mit.notepad.activities.NoteViewActivity so this file can use that type by its short name.
import com.mit.notepad.activities.NoteViewActivity;
// Imports com.mit.notepad.activities.TodoEditorActivity so this file can use that type by its short name.
import com.mit.notepad.activities.TodoEditorActivity;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

/** Creates notification channels and posts system notifications. */
// Declares class NotificationHelper to group related state and behavior.
public final class NotificationHelper {
    // Declares NotificationHelper() to implement this part of the component.
    private NotificationHelper() { }

    // Creates the notification channels used by the app.
    public static void createChannels(Context context) {
        // If Build.VERSION.SDK_INT < Build.VERSION_CODES.O is true, return immediately.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        // Initializes nm with context.getSystemService(NotificationManager.class) so later statements can use the computed value.
        NotificationManager nm = context.getSystemService(NotificationManager.class);
        // If nm == null is true, return immediately.
        if (nm == null) return;
        // Initializes reminders with new NotificationChannel(Constants.CHANNEL_REMINDERS, so later statements can use the computed value.
        NotificationChannel reminders = new NotificationChannel(Constants.CHANNEL_REMINDERS,
                // Calls context.getString with R.string.channel_reminders), NotificationManager.IMPORTANCE_HIGH to perform the operation shown.
                context.getString(R.string.channel_reminders), NotificationManager.IMPORTANCE_HIGH);
        // Calls reminders.setDescription with context.getString(R.string.channel_reminders_desc to perform the operation shown.
        reminders.setDescription(context.getString(R.string.channel_reminders_desc));
        // Initializes alarms with new NotificationChannel(Constants.CHANNEL_ALARMS, so later statements can use the computed value.
        NotificationChannel alarms = new NotificationChannel(Constants.CHANNEL_ALARMS,
                // Calls context.getString with R.string.channel_alarms), NotificationManager.IMPORTANCE_HIGH to perform the operation shown.
                context.getString(R.string.channel_alarms), NotificationManager.IMPORTANCE_HIGH);
        // Calls alarms.setDescription with context.getString(R.string.channel_alarms_desc to perform the operation shown.
        alarms.setDescription(context.getString(R.string.channel_alarms_desc));
        // Calls nm.createNotificationChannel with reminders to perform the operation shown.
        nm.createNotificationChannel(reminders);
        // Calls nm.createNotificationChannel with alarms to perform the operation shown.
        nm.createNotificationChannel(alarms);
    // Closes the current method, control-flow, or type block.
    }

    /** True if the app may currently post notifications (permission + system switch). */
    // Checks whether post.
    public static boolean canPost(Context context) {
        // Calls if with Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU to perform the operation shown.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                // Requires the additional condition ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) to be true as well.
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                // Continues the expression above with != PackageManager.PERMISSION_GRANTED) {.
                != PackageManager.PERMISSION_GRANTED) {
            // Returns false to the caller.
            return false;
        // Closes the current method, control-flow, or type block.
        }
        // Returns NotificationManagerCompat.from(context).areNotificationsEnabled() to the caller.
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    // Closes the current method, control-flow, or type block.
    }

    /**
     * Posts a system notification. Silently does nothing when notifications are blocked; the
     * in-app history entry has already been stored by the caller.
     */
    // Declares show() to implement this part of the component.
    public static void show(Context context, int notificationId, String category, String title,
                            // Declares todoId for use by the surrounding method or class.
                            String text, long noteId, long todoId, long historyId) {
        // If !canPost(context) is true, return immediately.
        if (!canPost(context)) return;

        // Declares target for use by the surrounding method or class.
        Intent target;
        // If noteId > 0 is true, { immediately.
        if (noteId > 0) {
            // Stores the result of new Intent(context, NoteViewActivity.class).putExtra(Constants.EXTRA_NOTE_ID, noteId) in target for use by later logic.
            target = new Intent(context, NoteViewActivity.class).putExtra(Constants.EXTRA_NOTE_ID, noteId);
        // Checks the alternate condition (todoId > 0) after the earlier branch did not run.
        } else if (todoId > 0) {
            // Stores the result of new Intent(context, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, todoId) in target for use by later logic.
            target = new Intent(context, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, todoId);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of new Intent(context, MainActivity.class) in target for use by later logic.
            target = new Intent(context, MainActivity.class);
        // Closes the current method, control-flow, or type block.
        }
        // Calls target.putExtra with Constants.EXTRA_NOTIFICATION_ID, historyId to perform the operation shown.
        target.putExtra(Constants.EXTRA_NOTIFICATION_ID, historyId);

        // Initializes stack with TaskStackBuilder.create(context) so later statements can use the computed value.
        TaskStackBuilder stack = TaskStackBuilder.create(context);
        // Calls stack.addNextIntentWithParentStack with target to perform the operation shown.
        stack.addNextIntentWithParentStack(target);
        // Initializes contentIntent with stack.getPendingIntent(notificationId, so later statements can use the computed value.
        PendingIntent contentIntent = stack.getPendingIntent(notificationId,
                // Executes PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE) as a statement.
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Initializes channel with Constants.CATEGORY_ALARM.equals(category) so later statements can use the computed value.
        String channel = Constants.CATEGORY_ALARM.equals(category)
                // Executes ? Constants.CHANNEL_ALARMS : Constants.CHANNEL_REMINDERS as a statement.
                ? Constants.CHANNEL_ALARMS : Constants.CHANNEL_REMINDERS;
        // Initializes builder with new NotificationCompat.Builder(context, channel) so later statements can use the computed value.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                // Calls setSmallIcon on the object from the preceding line with the shown arguments.
                .setSmallIcon(R.drawable.ic_notification)
                // Calls setContentTitle on the object from the preceding line with the shown arguments.
                .setContentTitle(title)
                // Calls setContentText on the object from the preceding line with the shown arguments.
                .setContentText(text)
                // Calls setStyle on the object from the preceding line with the shown arguments.
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                // Calls setPriority on the object from the preceding line with the shown arguments.
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Calls setCategory on the object from the preceding line with the shown arguments.
                .setCategory(Constants.CATEGORY_ALARM.equals(category)
                        // Continues the expression above with ? NotificationCompat.CATEGORY_ALARM : NotificationCompat.CATEGORY_REMINDER).
                        ? NotificationCompat.CATEGORY_ALARM : NotificationCompat.CATEGORY_REMINDER)
                // Calls setAutoCancel on the object from the preceding line with the shown arguments.
                .setAutoCancel(true)
                // Calls setContentIntent on the object from the preceding line with the shown arguments.
                .setContentIntent(contentIntent);
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls NotificationManagerCompat.from with context).notify(notificationId, builder.build( to perform the operation shown.
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
        // Calls catch with SecurityException ignored) { to perform the operation shown.
        } catch (SecurityException ignored) {
            // Permission revoked at the last moment; history entry already exists.
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
