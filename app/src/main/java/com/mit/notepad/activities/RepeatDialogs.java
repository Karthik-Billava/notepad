package com.mit.notepad.activities;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.UiUtils;

/** Dialogs for choosing a repeat rule. */
public final class RepeatDialogs {
    private RepeatDialogs() { }

    public interface IntervalCallback {
        void onInterval(int days);

        void onCancel();
    }

    public interface RepeatCallback {
        void onRepeat(String type, int interval);
    }

    /** Asks for "repeat every N days" (1-365). */
    public static void askInterval(Context ctx, int current, IntervalCallback callback) {
        EditText input = new EditText(ctx);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(Math.max(1, current)));
        input.setSelection(input.getText().length());
        FrameLayout holder = new FrameLayout(ctx);
        int pad = UiUtils.dp(ctx, 20);
        holder.setPadding(pad, UiUtils.dp(ctx, 8), pad, 0);
        holder.addView(input);
        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.repeat_custom_title)
                .setMessage(R.string.repeat_custom_message)
                .setView(holder)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, (d, w) -> callback.onCancel())
                .setOnCancelListener(d -> callback.onCancel())
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int days;
            try {
                days = Integer.parseInt(input.getText().toString().trim());
            } catch (NumberFormatException e) {
                days = 0;
            }
            if (days < 1 || days > 365) {
                input.setError(ctx.getString(R.string.error_interval_range));
                return;
            }
            dialog.dismiss();
            callback.onInterval(days);
        }));
        dialog.show();
    }

    /** None / Daily / Weekly / Custom chooser. */
    public static void pickRepeat(Context ctx, String currentType, int currentInterval, RepeatCallback callback) {
        String[] labels = {ctx.getString(R.string.repeat_none), ctx.getString(R.string.repeat_daily),
                ctx.getString(R.string.repeat_weekly), ctx.getString(R.string.repeat_custom)};
        String[] types = {Constants.REPEAT_NONE, Constants.REPEAT_DAILY, Constants.REPEAT_WEEKLY, Constants.REPEAT_CUSTOM};
        int checked = 0;
        for (int i = 0; i < types.length; i++) if (types[i].equals(currentType)) checked = i;
        new MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.repeat_optional)
                .setSingleChoiceItems(labels, checked, (d, which) -> {
                    d.dismiss();
                    if (Constants.REPEAT_CUSTOM.equals(types[which])) {
                        askInterval(ctx, currentInterval, new IntervalCallback() {
                            @Override public void onInterval(int days) { callback.onRepeat(Constants.REPEAT_CUSTOM, days); }
                            @Override public void onCancel() { }
                        });
                    } else {
                        callback.onRepeat(types[which], 1);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
