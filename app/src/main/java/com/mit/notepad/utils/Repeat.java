package com.mit.notepad.utils;

import java.util.Calendar;

/** Pure repeat-interval logic (no Android dependencies, unit-testable). */
public final class Repeat {
    private Repeat() { }

    public static boolean isRepeating(String type) {
        return Constants.REPEAT_DAILY.equals(type)
                || Constants.REPEAT_WEEKLY.equals(type)
                || Constants.REPEAT_CUSTOM.equals(type);
    }

    /** Length of one repeat step in days, or 0 for non-repeating alarms. */
    public static int stepDays(String type, int customInterval) {
        if (Constants.REPEAT_DAILY.equals(type)) return 1;
        if (Constants.REPEAT_WEEKLY.equals(type)) return 7;
        if (Constants.REPEAT_CUSTOM.equals(type)) return Math.max(1, customInterval);
        return 0;
    }

    /**
     * Returns the first occurrence strictly after both {@code scheduled} and {@code now},
     * or -1 if the alarm does not repeat. Calendar arithmetic keeps the wall-clock time
     * stable across daylight-saving changes.
     */
    public static long nextOccurrence(long scheduled, String type, int customInterval, long now) {
        int step = stepDays(type, customInterval);
        if (step <= 0) return -1;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(scheduled);
        do {
            c.add(Calendar.DAY_OF_YEAR, step);
        } while (c.getTimeInMillis() <= now);
        return c.getTimeInMillis();
    }
}
