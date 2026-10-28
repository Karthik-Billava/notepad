// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports android.graphics.Color so this file can use that type by its short name.
import android.graphics.Color;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;

/** Maps stored tag colour/icon keys to Android resources. */
// Declares class TagStyle to group related state and behavior.
public final class TagStyle {
    // Declares TagStyle() to implement this part of the component.
    private TagStyle() { }

    // Initializes PALETTE with { so later statements can use the computed value.
    public static final String[] PALETTE = {
            // Supplies "#7C6CF0", "#F0476B", "#F5B731", "#3B8EF0" as the next argument to the call above.
            "#7C6CF0", "#F0476B", "#F5B731", "#3B8EF0",
            // Continues the expression above with "#3DBE7A", "#F28B50", "#9B59B6", "#16A5A5".
            "#3DBE7A", "#F28B50", "#9B59B6", "#16A5A5"
    // Opens the block whose statements implement the declaration or branch above.
    };

    // Declares iconRes() to implement this part of the component.
    public static int iconRes(String key) {
        // If "school".equals(key) is true, return R.drawable.ic_school immediately.
        if ("school".equals(key)) return R.drawable.ic_school;
        // If "heart".equals(key) is true, return R.drawable.ic_favorite immediately.
        if ("heart".equals(key)) return R.drawable.ic_favorite;
        // If "work".equals(key) is true, return R.drawable.ic_work immediately.
        if ("work".equals(key)) return R.drawable.ic_work;
        // If "bulb".equals(key) is true, return R.drawable.ic_lightbulb immediately.
        if ("bulb".equals(key)) return R.drawable.ic_lightbulb;
        // If "star".equals(key) is true, return R.drawable.ic_star immediately.
        if ("star".equals(key)) return R.drawable.ic_star;
        // Returns R.drawable.ic_label to the caller.
        return R.drawable.ic_label;
    // Closes the current method, control-flow, or type block.
    }

    // Declares color() to implement this part of the component.
    public static int color(String hex) {
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Returns Color.parseColor(hex) to the caller.
            return Color.parseColor(hex);
        // Calls catch with IllegalArgumentException | NullPointerException e) { to perform the operation shown.
        } catch (IllegalArgumentException | NullPointerException e) {
            // Returns Color.parseColor("#7C6CF0") to the caller.
            return Color.parseColor("#7C6CF0");
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** The tag colour with the given alpha (0-255) applied. */
    // Declares withAlpha() to implement this part of the component.
    public static int withAlpha(String hex, int alpha) {
        // Initializes c with color(hex) so later statements can use the computed value.
        int c = color(hex);
        // Returns Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c)) to the caller.
        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
    // Closes the current method, control-flow, or type block.
    }

    // Declares soft() to implement this part of the component.
    public static int soft(String hex) {
        // Returns withAlpha(hex, 0x2E) to the caller.
        return withAlpha(hex, 0x2E);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
