// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

/**
 * Single source of truth for note statistics.
 * <ul>
 *   <li>Word count: number of whitespace-separated tokens in the trimmed text (0 for blank text).</li>
 *   <li>Character count: number of Unicode code points in the text, including spaces and line breaks.</li>
 * </ul>
 */
// Declares class TextStats to group related state and behavior.
public final class TextStats {
    // Declares TextStats() to implement this part of the component.
    private TextStats() { }

    // Declares wordCount() to implement this part of the component.
    public static int wordCount(String text) {
        // If text == null is true, return 0 immediately.
        if (text == null) return 0;
        // Initializes trimmed with text.trim() so later statements can use the computed value.
        String trimmed = text.trim();
        // If trimmed.isEmpty() is true, return 0 immediately.
        if (trimmed.isEmpty()) return 0;
        // Returns trimmed.split("\\s+").length to the caller.
        return trimmed.split("\\s+").length;
    // Closes the current method, control-flow, or type block.
    }

    // Declares charCount() to implement this part of the component.
    public static int charCount(String text) {
        // If text == null is true, return 0 immediately.
        if (text == null) return 0;
        // Returns text.codePointCount(0, text.length()) to the caller.
        return text.codePointCount(0, text.length());
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
