// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.text.format.DateUtils so this file can use that type by its short name.
import android.text.format.DateUtils;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;

// Imports java.text.SimpleDateFormat so this file can use that type by its short name.
import java.text.SimpleDateFormat;
// Imports java.util.Calendar so this file can use that type by its short name.
import java.util.Calendar;
// Imports java.util.Date so this file can use that type by its short name.
import java.util.Date;
// Imports java.util.Locale so this file can use that type by its short name.
import java.util.Locale;

/** Date and time formatting helpers. */
// Declares class DateTimeUtils to group related state and behavior.
public final class DateTimeUtils {
    // Declares DateTimeUtils() to implement this part of the component.
    private DateTimeUtils() { }

    // Formats date time.
    public static String formatDateTime(long ms) {
        // Returns new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(new Date(ms)) to the caller.
        return new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault()).format(new Date(ms));
    // Closes the current method, control-flow, or type block.
    }

    // Formats date.
    public static String formatDate(long ms) {
        // Returns new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(ms)) to the caller.
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(ms));
    // Closes the current method, control-flow, or type block.
    }

    // Formats time.
    public static String formatTime(long ms) {
        // Returns new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(ms)) to the caller.
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(ms));
    // Closes the current method, control-flow, or type block.
    }

    /** Short stamp for lists: time for today, "Yesterday", otherwise "dd MMM". */
    // Declares listStamp() to implement this part of the component.
    public static String listStamp(Context ctx, long ms) {
        // Initializes now with Calendar.getInstance() so later statements can use the computed value.
        Calendar now = Calendar.getInstance();
        // Initializes then with Calendar.getInstance() so later statements can use the computed value.
        Calendar then = Calendar.getInstance();
        // Calls then.setTimeInMillis with ms to perform the operation shown.
        then.setTimeInMillis(ms);
        // If sameDay(now, then) is true, return formatTime(ms) immediately.
        if (sameDay(now, then)) return formatTime(ms);
        // Calls now.add with Calendar.DAY_OF_YEAR, -1 to perform the operation shown.
        now.add(Calendar.DAY_OF_YEAR, -1);
        // If sameDay(now, then) is true, return ctx.getString(R.string.yesterday) immediately.
        if (sameDay(now, then)) return ctx.getString(R.string.yesterday);
        // Returns new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(ms)) to the caller.
        return new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(ms));
    // Closes the current method, control-flow, or type block.
    }

    /** e.g. "5 minutes ago". */
    // Declares relative() to implement this part of the component.
    public static String relative(long ms) {
        // Returns DateUtils.getRelativeTimeSpanString(ms, System.currentTimeMillis(), to the caller.
        return DateUtils.getRelativeTimeSpanString(ms, System.currentTimeMillis(),
                // Calls .toString with  to perform the operation shown.
                DateUtils.MINUTE_IN_MILLIS).toString();
    // Closes the current method, control-flow, or type block.
    }

    // Starts of day.
    public static long startOfDay(long ms) {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with ms to perform the operation shown.
        c.setTimeInMillis(ms);
        // Calls c.set with Calendar.HOUR_OF_DAY, 0 to perform the operation shown.
        c.set(Calendar.HOUR_OF_DAY, 0);
        // Calls c.set with Calendar.MINUTE, 0 to perform the operation shown.
        c.set(Calendar.MINUTE, 0);
        // Calls c.set with Calendar.SECOND, 0 to perform the operation shown.
        c.set(Calendar.SECOND, 0);
        // Calls c.set with Calendar.MILLISECOND, 0 to perform the operation shown.
        c.set(Calendar.MILLISECOND, 0);
        // Returns c.getTimeInMillis() to the caller.
        return c.getTimeInMillis();
    // Closes the current method, control-flow, or type block.
    }

    // Declares endOfDay() to implement this part of the component.
    public static long endOfDay(long ms) {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with startOfDay(ms to perform the operation shown.
        c.setTimeInMillis(startOfDay(ms));
        // Calls c.add with Calendar.DAY_OF_YEAR, 1 to perform the operation shown.
        c.add(Calendar.DAY_OF_YEAR, 1);
        // Returns c.getTimeInMillis() - 1 to the caller.
        return c.getTimeInMillis() - 1;
    // Closes the current method, control-flow, or type block.
    }

    // Declares sameDay() to implement this part of the component.
    private static boolean sameDay(Calendar a, Calendar b) {
        // Returns a.get(Calendar.YEAR) == b.get(Calendar.YEAR) to the caller.
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                // Requires the additional condition a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR) to be true as well.
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    // Closes the current method, control-flow, or type block.
    }

    /** Next full hour after now. */
    // Declares nextFullHour() to implement this part of the component.
    public static long nextFullHour() {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.add with Calendar.HOUR_OF_DAY, 1 to perform the operation shown.
        c.add(Calendar.HOUR_OF_DAY, 1);
        // Calls c.set with Calendar.MINUTE, 0 to perform the operation shown.
        c.set(Calendar.MINUTE, 0);
        // Calls c.set with Calendar.SECOND, 0 to perform the operation shown.
        c.set(Calendar.SECOND, 0);
        // Calls c.set with Calendar.MILLISECOND, 0 to perform the operation shown.
        c.set(Calendar.MILLISECOND, 0);
        // Returns c.getTimeInMillis() to the caller.
        return c.getTimeInMillis();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
