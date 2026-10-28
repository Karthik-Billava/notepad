// Sets the package name to com.mit.notepad.alarms so this class belongs to the app module namespace.
package com.mit.notepad.alarms;

// Imports android.app.AlarmManager so this file can use that type by its short name.
import android.app.AlarmManager;
// Imports android.app.PendingIntent so this file can use that type by its short name.
import android.app.PendingIntent;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.net.Uri so this file can use that type by its short name.
import android.net.Uri;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;

// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.repositories.AlarmRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.AlarmRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

/**
 * Thin wrapper around AlarmManager.
 *
 * Every alarm uses its own database id as the PendingIntent request code (and in the intent data
 * URI), so alarms never overwrite or cancel each other. Scheduling the same alarm twice simply
 * replaces the previous PendingIntent (FLAG_UPDATE_CURRENT), which prevents duplicates.
 *
 * Exact delivery needs the "Alarms &amp; reminders" special access on Android 12+. When it is not
 * granted we fall back to setAndAllowWhileIdle(), which may be delayed by the system.
 */
// Declares class AlarmScheduler to group related state and behavior.
public final class AlarmScheduler {
    // Declares AlarmScheduler() to implement this part of the component.
    private AlarmScheduler() { }

    // Checks whether schedule exact.
    public static boolean canScheduleExact(Context context) {
        // If Build.VERSION.SDK_INT < Build.VERSION_CODES.S is true, return true immediately.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true;
        // Initializes am with (AlarmManager) context.getSystemService(Context.ALARM_SERVICE) so later statements can use the computed value.
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Returns am != null && am.canScheduleExactAlarms() to the caller.
        return am != null && am.canScheduleExactAlarms();
    // Closes the current method, control-flow, or type block.
    }

    // Builds intent.
    private static Intent buildIntent(Context context, long alarmId) {
        // Initializes intent with new Intent(context, AlarmReceiver.class) so later statements can use the computed value.
        Intent intent = new Intent(context, AlarmReceiver.class);
        // Calls intent.setAction with Constants.ACTION_ALARM to perform the operation shown.
        intent.setAction(Constants.ACTION_ALARM);
        // Calls intent.setData with Uri.parse("notepad://alarm/" + alarmId to perform the operation shown.
        intent.setData(Uri.parse("notepad://alarm/" + alarmId));
        // Calls intent.putExtra with Constants.EXTRA_ALARM_ID, alarmId to perform the operation shown.
        intent.putExtra(Constants.EXTRA_ALARM_ID, alarmId);
        // Returns intent to the caller.
        return intent;
    // Closes the current method, control-flow, or type block.
    }

    // Declares pending() to implement this part of the component.
    private static PendingIntent pending(Context context, long alarmId, int flags) {
        // Returns PendingIntent.getBroadcast(context, (int) alarmId, buildIntent(context, alarmId), to the caller.
        return PendingIntent.getBroadcast(context, (int) alarmId, buildIntent(context, alarmId),
                // Executes flags | PendingIntent.FLAG_IMMUTABLE) as a statement.
                flags | PendingIntent.FLAG_IMMUTABLE);
    // Closes the current method, control-flow, or type block.
    }

    /** Schedules (or re-schedules) the alarm. Disabled alarms are cancelled instead. */
    // Registers an enabled alarm with AlarmManager, using exact timing when permitted.
    public static void schedule(Context context, Alarm alarm) {
        // If !alarm.enabled is true, { immediately.
        if (!alarm.enabled) {
            // Calls cancel with context, alarm.id to perform the operation shown.
            cancel(context, alarm.id);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Initializes am with (AlarmManager) context.getSystemService(Context.ALARM_SERVICE) so later statements can use the computed value.
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // If am == null is true, return immediately.
        if (am == null) return;
        // Initializes pi with pending(context, alarm.id, PendingIntent.FLAG_UPDATE_CURRENT) so later statements can use the computed value.
        PendingIntent pi = pending(context, alarm.id, PendingIntent.FLAG_UPDATE_CURRENT);
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // If canScheduleExact(context) is true, { immediately.
            if (canScheduleExact(context)) {
                // Calls am.setExactAndAllowWhileIdle with AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi to perform the operation shown.
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Calls am.setAndAllowWhileIdle with AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi to perform the operation shown.
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi);
            // Closes the current method, control-flow, or type block.
            }
        // Calls catch with SecurityException e) { to perform the operation shown.
        } catch (SecurityException e) {
            // Exact-alarm access was revoked between the check and the call.
            // Calls am.setAndAllowWhileIdle with AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi to perform the operation shown.
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi);
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Cancels the PendingIntent and AlarmManager entry for the alarm id.
    public static void cancel(Context context, long alarmId) {
        // Initializes am with (AlarmManager) context.getSystemService(Context.ALARM_SERVICE) so later statements can use the computed value.
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Initializes pi with pending(context, alarmId, PendingIntent.FLAG_NO_CREATE) so later statements can use the computed value.
        PendingIntent pi = pending(context, alarmId, PendingIntent.FLAG_NO_CREATE);
        // If pi != null is true, { immediately.
        if (pi != null) {
            // If am != null is true, am.cancel(pi) immediately.
            if (am != null) am.cancel(pi);
            // Calls pi.cancel with  to perform the operation shown.
            pi.cancel();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** True if a PendingIntent for this alarm id currently exists. */
    // Checks whether scheduled.
    public static boolean isScheduled(Context context, long alarmId) {
        // Returns pending(context, alarmId, PendingIntent.FLAG_NO_CREATE) != null to the caller.
        return pending(context, alarmId, PendingIntent.FLAG_NO_CREATE) != null;
    // Closes the current method, control-flow, or type block.
    }

    /**
     * Reconciles stored alarms with AlarmManager (after reboot or app update). Alarms whose time
     * passed while the device was off are delivered immediately; repeating ones move forward.
     */
    // Reconciles stored alarms with AlarmManager after a reboot or app update.
    public static void rescheduleAll(Context context) {
        // Initializes repo with new AlarmRepository(context) so later statements can use the computed value.
        AlarmRepository repo = new AlarmRepository(context);
        // Initializes now with System.currentTimeMillis() so later statements can use the computed value.
        long now = System.currentTimeMillis();
        // Repeats the following block according to the initialization, condition, and update in Alarm a : repo.getEnabled().
        for (Alarm a : repo.getEnabled()) {
            // If a.scheduledAt <= now is true, { immediately.
            if (a.scheduledAt <= now) {
                // Calls AlarmDelivery.deliver with context, a.id to perform the operation shown.
                AlarmDelivery.deliver(context, a.id);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Calls schedule with context, a to perform the operation shown.
                schedule(context, a);
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
