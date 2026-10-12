package com.mit.notepad.utils;

/**
 * Single source of truth for note statistics.
 * <ul>
 *   <li>Word count: number of whitespace-separated tokens in the trimmed text (0 for blank text).</li>
 *   <li>Character count: number of Unicode code points in the text, including spaces and line breaks.</li>
 * </ul>
 */
public final class TextStats {
    private TextStats() { }

    public static int wordCount(String text) {
        if (text == null) return 0;
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return 0;
        return trimmed.split("\\s+").length;
    }

    public static int charCount(String text) {
        if (text == null) return 0;
        return text.codePointCount(0, text.length());
    }
}
