// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;

/** Human readable repeat descriptions. */
// Declares class RepeatLabels to group related state and behavior.
public final class RepeatLabels {
    // Declares RepeatLabels() to implement this part of the component.
    private RepeatLabels() { }

    // Declares label() to implement this part of the component.
    public static String label(Context ctx, String type, int interval) {
        // If Constants.REPEAT_DAILY.equals(type) is true, return ctx.getString(R.string.repeat_daily) immediately.
        if (Constants.REPEAT_DAILY.equals(type)) return ctx.getString(R.string.repeat_daily);
        // If Constants.REPEAT_WEEKLY.equals(type) is true, return ctx.getString(R.string.repeat_weekly) immediately.
        if (Constants.REPEAT_WEEKLY.equals(type)) return ctx.getString(R.string.repeat_weekly);
        // If Constants.REPEAT_CUSTOM.equals(type) is true, { immediately.
        if (Constants.REPEAT_CUSTOM.equals(type)) {
            // Returns ctx.getResources().getQuantityString(R.plurals.repeat_every_days, to the caller.
            return ctx.getResources().getQuantityString(R.plurals.repeat_every_days,
                    // Calls Math.max with 1, interval), Math.max(1, interval to perform the operation shown.
                    Math.max(1, interval), Math.max(1, interval));
        // Closes the current method, control-flow, or type block.
        }
        // Returns ctx.getString(R.string.repeat_none) to the caller.
        return ctx.getString(R.string.repeat_none);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
