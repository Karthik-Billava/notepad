package com.mit.notepad.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.mit.notepad.models.Alarm;
import com.mit.notepad.repositories.AlarmRepository;
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
public final class AlarmScheduler {
    private AlarmScheduler() { }

    public static boolean canScheduleExact(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return am != null && am.canScheduleExactAlarms();
    }

    private static Intent buildIntent(Context context, long alarmId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(Constants.ACTION_ALARM);
        intent.setData(Uri.parse("notepad://alarm/" + alarmId));
        intent.putExtra(Constants.EXTRA_ALARM_ID, alarmId);
        return intent;
    }

    private static PendingIntent pending(Context context, long alarmId, int flags) {
        return PendingIntent.getBroadcast(context, (int) alarmId, buildIntent(context, alarmId),
                flags | PendingIntent.FLAG_IMMUTABLE);
    }

    /** Schedules (or re-schedules) the alarm. Disabled alarms are cancelled instead. */
    public static void schedule(Context context, Alarm alarm) {
        if (!alarm.enabled) {
            cancel(context, alarm.id);
            return;
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        PendingIntent pi = pending(context, alarm.id, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            if (canScheduleExact(context)) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi);
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi);
            }
        } catch (SecurityException e) {
            // Exact-alarm access was revoked between the check and the call.
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.scheduledAt, pi);
        }
    }

    public static void cancel(Context context, long alarmId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = pending(context, alarmId, PendingIntent.FLAG_NO_CREATE);
        if (pi != null) {
            if (am != null) am.cancel(pi);
            pi.cancel();
        }
    }

    /** True if a PendingIntent for this alarm id currently exists. */
    public static boolean isScheduled(Context context, long alarmId) {
        return pending(context, alarmId, PendingIntent.FLAG_NO_CREATE) != null;
    }

    /**
     * Reconciles stored alarms with AlarmManager (after reboot or app update). Alarms whose time
     * passed while the device was off are delivered immediately; repeating ones move forward.
     */
    public static void rescheduleAll(Context context) {
        AlarmRepository repo = new AlarmRepository(context);
        long now = System.currentTimeMillis();
        for (Alarm a : repo.getEnabled()) {
            if (a.scheduledAt <= now) {
                AlarmDelivery.deliver(context, a.id);
            } else {
                schedule(context, a);
            }
        }
    }
}
