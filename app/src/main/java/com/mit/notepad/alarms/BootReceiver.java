package com.mit.notepad.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Re-registers alarms after a reboot or app update (AlarmManager forgets them). */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            AlarmScheduler.rescheduleAll(context.getApplicationContext());
        }
    }
}
