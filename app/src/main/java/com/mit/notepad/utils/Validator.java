package com.mit.notepad.utils;

import com.mit.notepad.R;

/** Input validation. Each method returns a string resource id for the error, or 0 if valid. */
public final class Validator {
    private Validator() { }

    public static int titleError(String title) {
        if (title == null || title.trim().isEmpty()) return R.string.error_title_required;
        if (title.trim().length() > Constants.MAX_TITLE_LENGTH) return R.string.error_title_too_long;
        return 0;
    }

    public static int tagNameError(String name) {
        if (name == null || name.trim().isEmpty()) return R.string.error_tag_name_required;
        if (name.trim().length() > Constants.MAX_TAG_NAME_LENGTH) return R.string.error_tag_name_too_long;
        return 0;
    }
}
