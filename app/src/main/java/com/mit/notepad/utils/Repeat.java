// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports java.util.Calendar so this file can use that type by its short name.
import java.util.Calendar;

/** Pure repeat-interval logic (no Android dependencies, unit-testable). */
// Declares class Repeat to group related state and behavior.
public final class Repeat {
    // Declares Repeat() to implement this part of the component.
    private Repeat() { }

    // Returns whether the repeat type represents a recurring schedule.
    public static boolean isRepeating(String type) {
        // Returns Constants.REPEAT_DAILY.equals(type) to the caller.
        return Constants.REPEAT_DAILY.equals(type)
                // Also accepts the expression Constants.REPEAT_WEEKLY.equals(type) as a true condition.
                || Constants.REPEAT_WEEKLY.equals(type)
                // Also accepts the expression Constants.REPEAT_CUSTOM.equals(type) as a true condition.
                || Constants.REPEAT_CUSTOM.equals(type);
    // Closes the current method, control-flow, or type block.
    }

    /** Length of one repeat step in days, or 0 for non-repeating alarms. */
    // Converts the repeat type into a day interval and enforces a positive custom interval.
    public static int stepDays(String type, int customInterval) {
        // If Constants.REPEAT_DAILY.equals(type) is true, return 1 immediately.
        if (Constants.REPEAT_DAILY.equals(type)) return 1;
        // If Constants.REPEAT_WEEKLY.equals(type) is true, return 7 immediately.
        if (Constants.REPEAT_WEEKLY.equals(type)) return 7;
        // If Constants.REPEAT_CUSTOM.equals(type) is true, return Math.max(1, customInterval) immediately.
        if (Constants.REPEAT_CUSTOM.equals(type)) return Math.max(1, customInterval);
        // Returns 0 to the caller.
        return 0;
    // Closes the current method, control-flow, or type block.
    }

    /**
     * Returns the first occurrence strictly after both {@code scheduled} and {@code now},
     * or -1 if the alarm does not repeat. Calendar arithmetic keeps the wall-clock time
     * stable across daylight-saving changes.
     */
    // Advances the scheduled time by its repeat interval until it is after now; returns -1 for a one-time alarm.
    public static long nextOccurrence(long scheduled, String type, int customInterval, long now) {
        // Initializes step with stepDays(type, customInterval) so later statements can use the computed value.
        int step = stepDays(type, customInterval);
        // If step <= 0 is true, return -1 immediately.
        if (step <= 0) return -1;
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with scheduled to perform the operation shown.
        c.setTimeInMillis(scheduled);
        // Starts a loop that checks its continuation condition after each iteration.
        do {
            // Calls c.add with Calendar.DAY_OF_YEAR, step to perform the operation shown.
            c.add(Calendar.DAY_OF_YEAR, step);
        // Continues the do-while loop when c.getTimeInMillis() <= now is true.
        } while (c.getTimeInMillis() <= now);
        // Returns c.getTimeInMillis() to the caller.
        return c.getTimeInMillis();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
