package com.mit.notepad.utils;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.mit.notepad.R;
import com.mit.notepad.repositories.SettingsRepository;

/** Applies the user's background theme setting. */
public final class ThemeUtils {
    private ThemeUtils() { }

    public static String currentKey(Context ctx) {
        return new SettingsRepository(ctx).get(Constants.SETTING_THEME, Constants.THEME_SYSTEM);
    }

    public static void applyNightMode(Context ctx) {
        String key = currentKey(ctx);
        int mode;
        if (Constants.THEME_DARK.equals(key)) {
            mode = AppCompatDelegate.MODE_NIGHT_YES;
        } else if (Constants.THEME_LIGHT.equals(key) || Constants.THEME_LAVENDER.equals(key)) {
            mode = AppCompatDelegate.MODE_NIGHT_NO;
        } else {
            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /** Must be called before super.onCreate(). Returns the key that was applied. */
    public static String apply(Activity activity) {
        applyNightMode(activity);
        String key = currentKey(activity);
        activity.setTheme(Constants.THEME_LAVENDER.equals(key)
                ? R.style.Theme_Notepad_Lavender : R.style.Theme_Notepad);
        return key;
    }
}
