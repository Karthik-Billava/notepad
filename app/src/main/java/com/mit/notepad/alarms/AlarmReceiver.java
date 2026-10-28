// Sets the package name to com.mit.notepad.alarms so this class belongs to the app module namespace.
package com.mit.notepad.alarms;

// Imports android.content.BroadcastReceiver so this file can use that type by its short name.
import android.content.BroadcastReceiver;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;

// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

/** Receives the AlarmManager broadcast and hands the alarm id to AlarmDelivery. */
// Declares class AlarmReceiver to group related state and behavior.
public class AlarmReceiver extends BroadcastReceiver {
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Processes the broadcast delivered to this receiver.
    public void onReceive(Context context, Intent intent) {
        // Initializes alarmId with intent.getLongExtra(Constants.EXTRA_ALARM_ID, -1) so later statements can use the computed value.
        long alarmId = intent.getLongExtra(Constants.EXTRA_ALARM_ID, -1);
        // If alarmId < 0 is true, return immediately.
        if (alarmId < 0) return;
        // Calls AlarmDelivery.deliver with context.getApplicationContext(), alarmId to perform the operation shown.
        AlarmDelivery.deliver(context.getApplicationContext(), alarmId);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
