// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.text.InputType so this file can use that type by its short name.
import android.text.InputType;
// Imports android.widget.EditText so this file can use that type by its short name.
import android.widget.EditText;
// Imports android.widget.FrameLayout so this file can use that type by its short name.
import android.widget.FrameLayout;

// Imports androidx.appcompat.app.AlertDialog so this file can use that type by its short name.
import androidx.appcompat.app.AlertDialog;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

/** Dialogs for choosing a repeat rule. */
// Declares class RepeatDialogs to group related state and behavior.
public final class RepeatDialogs {
    // Declares RepeatDialogs() to implement this part of the component.
    private RepeatDialogs() { }

    // Declares interface IntervalCallback to group related state and behavior.
    public interface IntervalCallback {
        // Calls onInterval with int days to perform the operation shown.
        void onInterval(int days);

        // Calls onCancel with  to perform the operation shown.
        void onCancel();
    // Closes the current method, control-flow, or type block.
    }

    // Declares interface RepeatCallback to group related state and behavior.
    public interface RepeatCallback {
        // Calls onRepeat with String type, int interval to perform the operation shown.
        void onRepeat(String type, int interval);
    // Closes the current method, control-flow, or type block.
    }

    /** Asks for "repeat every N days" (1-365). */
    // Prompts the user about interval.
    public static void askInterval(Context ctx, int current, IntervalCallback callback) {
        // Initializes input with new EditText(ctx) so later statements can use the computed value.
        EditText input = new EditText(ctx);
        // Calls input.setInputType with InputType.TYPE_CLASS_NUMBER to perform the operation shown.
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        // Calls input.setText with String.valueOf(Math.max(1, current to perform the operation shown.
        input.setText(String.valueOf(Math.max(1, current)));
        // Calls input.setSelection with input.getText().length( to perform the operation shown.
        input.setSelection(input.getText().length());
        // Initializes holder with new FrameLayout(ctx) so later statements can use the computed value.
        FrameLayout holder = new FrameLayout(ctx);
        // Initializes pad with UiUtils.dp(ctx, 20) so later statements can use the computed value.
        int pad = UiUtils.dp(ctx, 20);
        // Calls holder.setPadding with pad, UiUtils.dp(ctx, 8), pad, 0 to perform the operation shown.
        holder.setPadding(pad, UiUtils.dp(ctx, 8), pad, 0);
        // Calls holder.addView with input to perform the operation shown.
        holder.addView(input);
        // Initializes dialog with new MaterialAlertDialogBuilder(ctx) so later statements can use the computed value.
        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.repeat_custom_title)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(R.string.repeat_custom_message)
                // Calls setView on the object from the preceding line with the shown arguments.
                .setView(holder)
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.ok, null)
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, (d, w) -> callback.onCancel())
                // Calls setOnCancelListener on the object from the preceding line with the shown arguments.
                .setOnCancelListener(d -> callback.onCancel())
                // Calls create on the object from the preceding line with the shown arguments.
                .create();
        // Starts the callback body that handles the event or asynchronous result.
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Declares days for use by the surrounding method or class.
            int days;
            // Starts the operation whose failures are handled by the following catch clauses.
            try {
                // Stores the result of Integer.parseInt(input.getText().toString().trim()) in days for use by later logic.
                days = Integer.parseInt(input.getText().toString().trim());
            // Calls catch with NumberFormatException e) { to perform the operation shown.
            } catch (NumberFormatException e) {
                // Stores the result of 0 in days for use by later logic.
                days = 0;
            // Closes the current method, control-flow, or type block.
            }
            // If days < 1 || days > 365 is true, { immediately.
            if (days < 1 || days > 365) {
                // Calls input.setError with ctx.getString(R.string.error_interval_range to perform the operation shown.
                input.setError(ctx.getString(R.string.error_interval_range));
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
            // Calls dialog.dismiss with  to perform the operation shown.
            dialog.dismiss();
            // Calls callback.onInterval with days to perform the operation shown.
            callback.onInterval(days);
        // Executes })) as a statement.
        }));
        // Calls dialog.show with  to perform the operation shown.
        dialog.show();
    // Closes the current method, control-flow, or type block.
    }

    /** None / Daily / Weekly / Custom chooser. */
    // Prompts the user to choose repeat.
    public static void pickRepeat(Context ctx, String currentType, int currentInterval, RepeatCallback callback) {
        // Initializes labels with {ctx.getString(R.string.repeat_none), ctx.getString(R.string.repeat_daily), so later statements can use the computed value.
        String[] labels = {ctx.getString(R.string.repeat_none), ctx.getString(R.string.repeat_daily),
                // Calls ctx.getString with R.string.repeat_weekly), ctx.getString(R.string.repeat_custom)} to perform the operation shown.
                ctx.getString(R.string.repeat_weekly), ctx.getString(R.string.repeat_custom)};
        // Initializes types with {Constants.REPEAT_NONE, Constants.REPEAT_DAILY, Constants.REPEAT_WEEKLY, Constants.REPEAT_CUSTOM} so later statements can use the computed value.
        String[] types = {Constants.REPEAT_NONE, Constants.REPEAT_DAILY, Constants.REPEAT_WEEKLY, Constants.REPEAT_CUSTOM};
        // Initializes checked with 0 so later statements can use the computed value.
        int checked = 0;
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < types.length; i++) if (types[i].equals(currentType).
        for (int i = 0; i < types.length; i++) if (types[i].equals(currentType)) checked = i;
        // Creates the object described by new MaterialAlertDialogBuilder(ctx).
        new MaterialAlertDialogBuilder(ctx)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.repeat_optional)
                // Starts the callback body that handles the event or asynchronous result.
                .setSingleChoiceItems(labels, checked, (d, which) -> {
                    // Calls d.dismiss with  to perform the operation shown.
                    d.dismiss();
                    // If Constants.REPEAT_CUSTOM.equals(types[which]) is true, { immediately.
                    if (Constants.REPEAT_CUSTOM.equals(types[which])) {
                        // Calls askInterval with ctx, currentInterval, new IntervalCallback() { to perform the operation shown.
                        askInterval(ctx, currentInterval, new IntervalCallback() {
                            // Applies @Override public void onInterval metadata to the declaration that follows.
                            @Override public void onInterval(int days) { callback.onRepeat(Constants.REPEAT_CUSTOM, days); }
                            // Applies @Override public void onCancel metadata to the declaration that follows.
                            @Override public void onCancel() { }
                        // Executes }) as a statement.
                        });
                    // Runs the fallback branch when the preceding condition did not match.
                    } else {
                        // Calls callback.onRepeat with types[which], 1 to perform the operation shown.
                        callback.onRepeat(types[which], 1);
                    // Closes the current method, control-flow, or type block.
                    }
                // Continues the expression above with }).
                })
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
