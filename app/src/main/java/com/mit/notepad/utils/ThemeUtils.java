// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports android.app.Activity so this file can use that type by its short name.
import android.app.Activity;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;

// Imports androidx.appcompat.app.AppCompatDelegate so this file can use that type by its short name.
import androidx.appcompat.app.AppCompatDelegate;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;

/** Applies the user's background theme setting. */
// Declares class ThemeUtils to group related state and behavior.
public final class ThemeUtils {
    // Declares ThemeUtils() to implement this part of the component.
    private ThemeUtils() { }

    // Declares currentKey() to implement this part of the component.
    public static String currentKey(Context ctx) {
        // Returns new SettingsRepository(ctx).get(Constants.SETTING_THEME, Constants.THEME_SYSTEM) to the caller.
        return new SettingsRepository(ctx).get(Constants.SETTING_THEME, Constants.THEME_SYSTEM);
    // Closes the current method, control-flow, or type block.
    }

    // Applies night mode.
    public static void applyNightMode(Context ctx) {
        // Initializes key with currentKey(ctx) so later statements can use the computed value.
        String key = currentKey(ctx);
        // Declares mode for use by the surrounding method or class.
        int mode;
        // If Constants.THEME_DARK.equals(key) is true, { immediately.
        if (Constants.THEME_DARK.equals(key)) {
            // Stores the result of AppCompatDelegate.MODE_NIGHT_YES in mode for use by later logic.
            mode = AppCompatDelegate.MODE_NIGHT_YES;
        // Checks the alternate condition (Constants.THEME_LIGHT.equals(key) after the earlier branch did not run.
        } else if (Constants.THEME_LIGHT.equals(key) || Constants.THEME_LAVENDER.equals(key)) {
            // Stores the result of AppCompatDelegate.MODE_NIGHT_NO in mode for use by later logic.
            mode = AppCompatDelegate.MODE_NIGHT_NO;
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM in mode for use by later logic.
            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        // Closes the current method, control-flow, or type block.
        }
        // Calls AppCompatDelegate.setDefaultNightMode with mode to perform the operation shown.
        AppCompatDelegate.setDefaultNightMode(mode);
    // Closes the current method, control-flow, or type block.
    }

    /** Must be called before super.onCreate(). Returns the key that was applied. */
    // Declares apply() to implement this part of the component.
    public static String apply(Activity activity) {
        // Calls applyNightMode with activity to perform the operation shown.
        applyNightMode(activity);
        // Initializes key with currentKey(activity) so later statements can use the computed value.
        String key = currentKey(activity);
        // Calls activity.setTheme with Constants.THEME_LAVENDER.equals(key to perform the operation shown.
        activity.setTheme(Constants.THEME_LAVENDER.equals(key)
                // Executes ? R.style.Theme_Notepad_Lavender : R.style.Theme_Notepad) as a statement.
                ? R.style.Theme_Notepad_Lavender : R.style.Theme_Notepad);
        // Returns key to the caller.
        return key;
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
