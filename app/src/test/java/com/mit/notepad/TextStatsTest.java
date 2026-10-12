package com.mit.notepad;

import static org.junit.Assert.assertEquals;

import com.mit.notepad.utils.TextStats;

import org.junit.Test;

public class TextStatsTest {
    @Test
    public void wordCount_countsWhitespaceSeparatedTokens() {
        assertEquals(0, TextStats.wordCount(null));
        assertEquals(0, TextStats.wordCount("   \n "));
        assertEquals(1, TextStats.wordCount("hello"));
        assertEquals(4, TextStats.wordCount("  one two\nthree\tfour "));
    }

    @Test
    public void charCount_includesSpacesAndLineBreaks() {
        assertEquals(0, TextStats.charCount(null));
        assertEquals(11, TextStats.charCount("hello world"));
        assertEquals(3, TextStats.charCount("a\nb"));
    }
}
