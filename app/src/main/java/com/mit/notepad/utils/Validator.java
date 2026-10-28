// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;

/** Input validation. Each method returns a string resource id for the error, or 0 if valid. */
// Declares class Validator to group related state and behavior.
public final class Validator {
    // Declares Validator() to implement this part of the component.
    private Validator() { }

    // Declares titleError() to implement this part of the component.
    public static int titleError(String title) {
        // If title == null || title.trim().isEmpty() is true, return R.string.error_title_required immediately.
        if (title == null || title.trim().isEmpty()) return R.string.error_title_required;
        // If title.trim().length() > Constants.MAX_TITLE_LENGTH is true, return R.string.error_title_too_long immediately.
        if (title.trim().length() > Constants.MAX_TITLE_LENGTH) return R.string.error_title_too_long;
        // Returns 0 to the caller.
        return 0;
    // Closes the current method, control-flow, or type block.
    }

    // Declares tagNameError() to implement this part of the component.
    public static int tagNameError(String name) {
        // If name == null || name.trim().isEmpty() is true, return R.string.error_tag_name_required immediately.
        if (name == null || name.trim().isEmpty()) return R.string.error_tag_name_required;
        // If name.trim().length() > Constants.MAX_TAG_NAME_LENGTH is true, return R.string.error_tag_name_too_long immediately.
        if (name.trim().length() > Constants.MAX_TAG_NAME_LENGTH) return R.string.error_tag_name_too_long;
        // Returns 0 to the caller.
        return 0;
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
