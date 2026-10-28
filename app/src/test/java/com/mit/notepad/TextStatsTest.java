// Sets the package name to com.mit.notepad so this class belongs to the app module namespace.
package com.mit.notepad;

// Imports static org.junit.Assert.assertEquals so this file can use that type by its short name.
import static org.junit.Assert.assertEquals;

// Imports com.mit.notepad.utils.TextStats so this file can use that type by its short name.
import com.mit.notepad.utils.TextStats;

// Imports org.junit.Test so this file can use that type by its short name.
import org.junit.Test;

// Declares class TextStatsTest to group related state and behavior.
public class TextStatsTest {
    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of word count_counts whitespace separated tokens.
    public void wordCount_countsWhitespaceSeparatedTokens() {
        // Calls assertEquals with 0, TextStats.wordCount(null to perform the operation shown.
        assertEquals(0, TextStats.wordCount(null));
        // Calls assertEquals with 0, TextStats.wordCount("   \n " to perform the operation shown.
        assertEquals(0, TextStats.wordCount("   \n "));
        // Calls assertEquals with 1, TextStats.wordCount("hello" to perform the operation shown.
        assertEquals(1, TextStats.wordCount("hello"));
        // Calls assertEquals with 4, TextStats.wordCount("  one two\nthree\tfour " to perform the operation shown.
        assertEquals(4, TextStats.wordCount("  one two\nthree\tfour "));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of char count_includes spaces and line breaks.
    public void charCount_includesSpacesAndLineBreaks() {
        // Calls assertEquals with 0, TextStats.charCount(null to perform the operation shown.
        assertEquals(0, TextStats.charCount(null));
        // Calls assertEquals with 11, TextStats.charCount("hello world" to perform the operation shown.
        assertEquals(11, TextStats.charCount("hello world"));
        // Calls assertEquals with 3, TextStats.charCount("a\nb" to perform the operation shown.
        assertEquals(3, TextStats.charCount("a\nb"));
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
