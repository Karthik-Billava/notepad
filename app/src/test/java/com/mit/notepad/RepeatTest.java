// Sets the package name to com.mit.notepad so this class belongs to the app module namespace.
package com.mit.notepad;

// Imports static org.junit.Assert.assertEquals so this file can use that type by its short name.
import static org.junit.Assert.assertEquals;
// Imports static org.junit.Assert.assertTrue so this file can use that type by its short name.
import static org.junit.Assert.assertTrue;

// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.Repeat so this file can use that type by its short name.
import com.mit.notepad.utils.Repeat;

// Imports org.junit.Test so this file can use that type by its short name.
import org.junit.Test;

// Imports java.util.Calendar so this file can use that type by its short name.
import java.util.Calendar;

// Declares class RepeatTest to group related state and behavior.
public class RepeatTest {
    // Tests the expected behavior of at.
    private static long at(int y, int m, int d, int h) {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.clear with  to perform the operation shown.
        c.clear();
        // Calls c.set with y, m, d, h, 0, 0 to perform the operation shown.
        c.set(y, m, d, h, 0, 0);
        // Returns c.getTimeInMillis() to the caller.
        return c.getTimeInMillis();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of non repeating alarm has no next occurrence.
    public void nonRepeatingAlarmHasNoNextOccurrence() {
        // Calls assertEquals with -1, Repeat.nextOccurrence(at(2026, 8, 9, 9), Constants.REPEAT_NONE, 1, at(2026, 8, 9, 10 to perform the operation shown.
        assertEquals(-1, Repeat.nextOccurrence(at(2026, 8, 9, 9), Constants.REPEAT_NONE, 1, at(2026, 8, 9, 10)));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of daily alarm moves to next day after now.
    public void dailyAlarmMovesToNextDayAfterNow() {
        // Initializes next with Repeat.nextOccurrence(at(2026, 8, 9, 9), Constants.REPEAT_DAILY, 1, at(2026, 8, 9, 9)) so later statements can use the computed value.
        long next = Repeat.nextOccurrence(at(2026, 8, 9, 9), Constants.REPEAT_DAILY, 1, at(2026, 8, 9, 9));
        // Calls assertEquals with at(2026, 8, 10, 9), next to perform the operation shown.
        assertEquals(at(2026, 8, 10, 9), next);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of missed weekly alarm skips to future occurrence.
    public void missedWeeklyAlarmSkipsToFutureOccurrence() {
        // Initializes next with Repeat.nextOccurrence(at(2026, 8, 1, 9), Constants.REPEAT_WEEKLY, 1, at(2026, 8, 20, 12)) so later statements can use the computed value.
        long next = Repeat.nextOccurrence(at(2026, 8, 1, 9), Constants.REPEAT_WEEKLY, 1, at(2026, 8, 20, 12));
        // Calls assertEquals with at(2026, 8, 22, 9), next to perform the operation shown.
        assertEquals(at(2026, 8, 22, 9), next);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of custom interval uses given number of days.
    public void customIntervalUsesGivenNumberOfDays() {
        // Initializes next with Repeat.nextOccurrence(at(2026, 8, 1, 9), Constants.REPEAT_CUSTOM, 3, at(2026, 8, 1, 10)) so later statements can use the computed value.
        long next = Repeat.nextOccurrence(at(2026, 8, 1, 9), Constants.REPEAT_CUSTOM, 3, at(2026, 8, 1, 10));
        // Calls assertEquals with at(2026, 8, 4, 9), next to perform the operation shown.
        assertEquals(at(2026, 8, 4, 9), next);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of next occurrence is always after scheduled time.
    public void nextOccurrenceIsAlwaysAfterScheduledTime() {
        // Initializes scheduled with at(2026, 8, 9, 9) so later statements can use the computed value.
        long scheduled = at(2026, 8, 9, 9);
        // Calls assertTrue with Repeat.nextOccurrence(scheduled, Constants.REPEAT_DAILY, 1, scheduled - 1000) > scheduled to perform the operation shown.
        assertTrue(Repeat.nextOccurrence(scheduled, Constants.REPEAT_DAILY, 1, scheduled - 1000) > scheduled);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
