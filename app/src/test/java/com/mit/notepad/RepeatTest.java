package com.mit.notepad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.Repeat;

import org.junit.Test;

import java.util.Calendar;

public class RepeatTest {
    private static long at(int y, int m, int d, int h) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(y, m, d, h, 0, 0);
        return c.getTimeInMillis();
    }

    @Test
    public void nonRepeatingAlarmHasNoNextOccurrence() {
        assertEquals(-1, Repeat.nextOccurrence(at(2026, 8, 9, 9), Constants.REPEAT_NONE, 1, at(2026, 8, 9, 10)));
    }

    @Test
    public void dailyAlarmMovesToNextDayAfterNow() {
        long next = Repeat.nextOccurrence(at(2026, 8, 9, 9), Constants.REPEAT_DAILY, 1, at(2026, 8, 9, 9));
        assertEquals(at(2026, 8, 10, 9), next);
    }

    @Test
    public void missedWeeklyAlarmSkipsToFutureOccurrence() {
        long next = Repeat.nextOccurrence(at(2026, 8, 1, 9), Constants.REPEAT_WEEKLY, 1, at(2026, 8, 20, 12));
        assertEquals(at(2026, 8, 22, 9), next);
    }

    @Test
    public void customIntervalUsesGivenNumberOfDays() {
        long next = Repeat.nextOccurrence(at(2026, 8, 1, 9), Constants.REPEAT_CUSTOM, 3, at(2026, 8, 1, 10));
        assertEquals(at(2026, 8, 4, 9), next);
    }

    @Test
    public void nextOccurrenceIsAlwaysAfterScheduledTime() {
        long scheduled = at(2026, 8, 9, 9);
        assertTrue(Repeat.nextOccurrence(scheduled, Constants.REPEAT_DAILY, 1, scheduled - 1000) > scheduled);
    }
}
