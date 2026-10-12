package com.mit.notepad.utils;

import android.content.Context;

import com.mit.notepad.R;

/** Human readable repeat descriptions. */
public final class RepeatLabels {
    private RepeatLabels() { }

    public static String label(Context ctx, String type, int interval) {
        if (Constants.REPEAT_DAILY.equals(type)) return ctx.getString(R.string.repeat_daily);
        if (Constants.REPEAT_WEEKLY.equals(type)) return ctx.getString(R.string.repeat_weekly);
        if (Constants.REPEAT_CUSTOM.equals(type)) {
            return ctx.getResources().getQuantityString(R.plurals.repeat_every_days,
                    Math.max(1, interval), Math.max(1, interval));
        }
        return ctx.getString(R.string.repeat_none);
    }
}
