package com.mit.notepad.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.mit.notepad.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Date and time formatting helpers. */
public final class DateTimeUtils {
    private DateTimeUtils() { }

    public static String formatDateTime(long ms) {
        return new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(new Date(ms));
    }

    public static String formatDate(long ms) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(ms));
    }

    public static String formatTime(long ms) {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(ms));
    }

    /** Short stamp for lists: time for today, "Yesterday", otherwise "dd MMM". */
    public static String listStamp(Context ctx, long ms) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTimeInMillis(ms);
        if (sameDay(now, then)) return formatTime(ms);
        now.add(Calendar.DAY_OF_YEAR, -1);
        if (sameDay(now, then)) return ctx.getString(R.string.yesterday);
        return new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(ms));
    }

    /** e.g. "5 minutes ago". */
    public static String relative(long ms) {
        return DateUtils.getRelativeTimeSpanString(ms, System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS).toString();
    }

    public static long startOfDay(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long endOfDay(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startOfDay(ms));
        c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTimeInMillis() - 1;
    }

    private static boolean sameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    /** Next full hour after now. */
    public static long nextFullHour() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
