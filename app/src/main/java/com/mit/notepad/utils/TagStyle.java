package com.mit.notepad.utils;

import android.graphics.Color;

import com.mit.notepad.R;

/** Maps stored tag colour/icon keys to Android resources. */
public final class TagStyle {
    private TagStyle() { }

    public static final String[] PALETTE = {
            "#7C6CF0", "#F0476B", "#F5B731", "#3B8EF0",
            "#3DBE7A", "#F28B50", "#9B59B6", "#16A5A5"
    };

    public static int iconRes(String key) {
        if ("school".equals(key)) return R.drawable.ic_school;
        if ("heart".equals(key)) return R.drawable.ic_favorite;
        if ("work".equals(key)) return R.drawable.ic_work;
        if ("bulb".equals(key)) return R.drawable.ic_lightbulb;
        if ("star".equals(key)) return R.drawable.ic_star;
        return R.drawable.ic_label;
    }

    public static int color(String hex) {
        try {
            return Color.parseColor(hex);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Color.parseColor("#7C6CF0");
        }
    }

    /** The tag colour with the given alpha (0-255) applied. */
    public static int withAlpha(String hex, int alpha) {
        int c = color(hex);
        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
    }

    public static int soft(String hex) {
        return withAlpha(hex, 0x2E);
    }
}
