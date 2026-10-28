package com.mit.notepad.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mit.notepad.R;
import com.mit.notepad.alarms.AlarmRingingService;

/** Full-screen alarm screen shown by Android when full-screen notifications are allowed. */
public class AlarmRingingActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.rgb(25, 28, 35));

        TextView label = new TextView(this);
        label.setText(getIntent().getStringExtra(AlarmRingingService.EXTRA_TITLE));
        label.setTextColor(Color.WHITE);
        label.setTextSize(22);
        label.setGravity(Gravity.CENTER);
        layout.addView(label, new LinearLayout.LayoutParams(-1, -2));

        TextView message = new TextView(this);
        message.setText(getIntent().getStringExtra(AlarmRingingService.EXTRA_MESSAGE));
        message.setTextColor(Color.LTGRAY);
        message.setTextSize(18);
        message.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(-1, -2);
        messageParams.topMargin = 16;
        layout.addView(message, messageParams);

        Button dismiss = new Button(this);
        dismiss.setText(R.string.dismiss_alarm);
        dismiss.setOnClickListener(v -> dismissAlarm());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(-1, -2);
        buttonParams.topMargin = 40;
        layout.addView(dismiss, buttonParams);
        setContentView(layout);
    }

    private void dismissAlarm() {
        startService(new Intent(this, AlarmRingingService.class)
                .setAction(AlarmRingingService.ACTION_STOP));
        finish();
    }
}
