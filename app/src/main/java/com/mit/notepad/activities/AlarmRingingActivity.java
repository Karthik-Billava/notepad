// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.app.Activity so this file can use that type by its short name.
import android.app.Activity;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.graphics.Color so this file can use that type by its short name.
import android.graphics.Color;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.view.Gravity so this file can use that type by its short name.
import android.view.Gravity;
// Imports android.view.WindowManager so this file can use that type by its short name.
import android.view.WindowManager;
// Imports android.widget.Button so this file can use that type by its short name.
import android.widget.Button;
// Imports android.widget.LinearLayout so this file can use that type by its short name.
import android.widget.LinearLayout;
// Imports android.widget.TextView so this file can use that type by its short name.
import android.widget.TextView;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.alarms.AlarmRingingService so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmRingingService;

/** Full-screen alarm screen shown by Android when full-screen notifications are allowed. */
// Declares class AlarmRingingActivity to group related state and behavior.
public class AlarmRingingActivity extends Activity {
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle state) {
        // Calls super.onCreate with state to perform the operation shown.
        super.onCreate(state);
        // If Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 is true, { immediately.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // Calls setShowWhenLocked with true to perform the operation shown.
            setShowWhenLocked(true);
            // Calls setTurnScreenOn with true to perform the operation shown.
            setTurnScreenOn(true);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls getWindow with ).addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED to perform the operation shown.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    // Continues the expression above with | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON.
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    // Executes | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) as a statement.
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Closes the current method, control-flow, or type block.
        }

        // Initializes layout with new LinearLayout(this) so later statements can use the computed value.
        LinearLayout layout = new LinearLayout(this);
        // Calls layout.setOrientation with LinearLayout.VERTICAL to perform the operation shown.
        layout.setOrientation(LinearLayout.VERTICAL);
        // Calls layout.setGravity with Gravity.CENTER to perform the operation shown.
        layout.setGravity(Gravity.CENTER);
        // Calls layout.setPadding with 32, 32, 32, 32 to perform the operation shown.
        layout.setPadding(32, 32, 32, 32);
        // Calls layout.setBackgroundColor with Color.rgb(25, 28, 35 to perform the operation shown.
        layout.setBackgroundColor(Color.rgb(25, 28, 35));

        // Initializes label with new TextView(this) so later statements can use the computed value.
        TextView label = new TextView(this);
        // Calls label.setText with getIntent().getStringExtra(AlarmRingingService.EXTRA_TITLE to perform the operation shown.
        label.setText(getIntent().getStringExtra(AlarmRingingService.EXTRA_TITLE));
        // Calls label.setTextColor with Color.WHITE to perform the operation shown.
        label.setTextColor(Color.WHITE);
        // Calls label.setTextSize with 22 to perform the operation shown.
        label.setTextSize(22);
        // Calls label.setGravity with Gravity.CENTER to perform the operation shown.
        label.setGravity(Gravity.CENTER);
        // Calls layout.addView with label, new LinearLayout.LayoutParams(-1, -2 to perform the operation shown.
        layout.addView(label, new LinearLayout.LayoutParams(-1, -2));

        // Initializes message with new TextView(this) so later statements can use the computed value.
        TextView message = new TextView(this);
        // Calls message.setText with getIntent().getStringExtra(AlarmRingingService.EXTRA_MESSAGE to perform the operation shown.
        message.setText(getIntent().getStringExtra(AlarmRingingService.EXTRA_MESSAGE));
        // Calls message.setTextColor with Color.LTGRAY to perform the operation shown.
        message.setTextColor(Color.LTGRAY);
        // Calls message.setTextSize with 18 to perform the operation shown.
        message.setTextSize(18);
        // Calls message.setGravity with Gravity.CENTER to perform the operation shown.
        message.setGravity(Gravity.CENTER);
        // Initializes messageParams with new LinearLayout.LayoutParams(-1, -2) so later statements can use the computed value.
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(-1, -2);
        // Stores the result of 16 in messageParams.topMargin for use by later logic.
        messageParams.topMargin = 16;
        // Calls layout.addView with message, messageParams to perform the operation shown.
        layout.addView(message, messageParams);

        // Initializes dismiss with new Button(this) so later statements can use the computed value.
        Button dismiss = new Button(this);
        // Calls dismiss.setText with R.string.dismiss_alarm to perform the operation shown.
        dismiss.setText(R.string.dismiss_alarm);
        // Calls dismiss.setOnClickListener with v -> dismissAlarm( to perform the operation shown.
        dismiss.setOnClickListener(v -> dismissAlarm());
        // Initializes buttonParams with new LinearLayout.LayoutParams(-1, -2) so later statements can use the computed value.
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(-1, -2);
        // Stores the result of 40 in buttonParams.topMargin for use by later logic.
        buttonParams.topMargin = 40;
        // Calls layout.addView with dismiss, buttonParams to perform the operation shown.
        layout.addView(dismiss, buttonParams);
        // Calls setContentView with layout to perform the operation shown.
        setContentView(layout);
    // Closes the current method, control-flow, or type block.
    }

    // Dismisses alarm.
    private void dismissAlarm() {
        // Calls startService with new Intent(this, AlarmRingingService.class to perform the operation shown.
        startService(new Intent(this, AlarmRingingService.class)
                // Calls setAction on the object from the preceding line with the shown arguments.
                .setAction(AlarmRingingService.ACTION_STOP));
        // Calls finish with  to perform the operation shown.
        finish();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
