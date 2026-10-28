// Sets the package name to com.mit.notepad.alarms so this class belongs to the app module namespace.
package com.mit.notepad.alarms;

// Imports android.content.BroadcastReceiver so this file can use that type by its short name.
import android.content.BroadcastReceiver;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;

/** Re-registers alarms after a reboot or app update (AlarmManager forgets them). */
// Declares class BootReceiver to group related state and behavior.
public class BootReceiver extends BroadcastReceiver {
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Processes the broadcast delivered to this receiver.
    public void onReceive(Context context, Intent intent) {
        // Initializes action with intent.getAction() so later statements can use the computed value.
        String action = intent.getAction();
        // If Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action) is true, { immediately.
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            // Calls AlarmScheduler.rescheduleAll with context.getApplicationContext( to perform the operation shown.
            AlarmScheduler.rescheduleAll(context.getApplicationContext());
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
