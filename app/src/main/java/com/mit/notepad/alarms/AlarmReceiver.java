package com.mit.notepad.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mit.notepad.utils.Constants;

/** Receives the AlarmManager broadcast and hands the alarm id to AlarmDelivery. */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra(Constants.EXTRA_ALARM_ID, -1);
        if (alarmId < 0) return;
        AlarmDelivery.deliver(context.getApplicationContext(), alarmId);
    }
}
