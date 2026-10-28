// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.content.pm.PackageInfo so this file can use that type by its short name.
import android.content.pm.PackageInfo;
// Imports android.net.Uri so this file can use that type by its short name.
import android.net.Uri;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.provider.Settings so this file can use that type by its short name.
import android.provider.Settings;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.databinding.ActivitySettingsBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivitySettingsBinding;
// Imports com.mit.notepad.notifications.NotificationHelper so this file can use that type by its short name.
import com.mit.notepad.notifications.NotificationHelper;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.ThemeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.ThemeUtils;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

/** Screen 12: appearance, notification preferences, privacy, about and exit. */
// Declares class SettingsActivity to group related state and behavior.
public class SettingsActivity extends BaseActivity {
    // Initializes THEME_KEYS with {Constants.THEME_SYSTEM, Constants.THEME_LIGHT, so later statements can use the computed value.
    private static final String[] THEME_KEYS = {Constants.THEME_SYSTEM, Constants.THEME_LIGHT,
            // Executes Constants.THEME_DARK, Constants.THEME_LAVENDER} as a statement.
            Constants.THEME_DARK, Constants.THEME_LAVENDER};
    // Initializes LIST_KEYS with {Constants.LIST_DEFAULT, Constants.LIST_COMPACT, so later statements can use the computed value.
    private static final String[] LIST_KEYS = {Constants.LIST_DEFAULT, Constants.LIST_COMPACT,
            // Executes Constants.LIST_DETAILED} as a statement.
            Constants.LIST_DETAILED};

    // Declares b for use by the surrounding method or class.
    private ActivitySettingsBinding b;
    // Declares settings for use by the surrounding method or class.
    private SettingsRepository settings;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivitySettingsBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new SettingsRepository(this) in settings for use by later logic.
        settings = new SettingsRepository(this);
        // Calls setupBackHeader with b.header, R.string.settings, R.string.subtitle_settings to perform the operation shown.
        setupBackHeader(b.header, R.string.settings, R.string.subtitle_settings);

        // Calls b.rowTheme.setOnClickListener with v -> chooseTheme( to perform the operation shown.
        b.rowTheme.setOnClickListener(v -> chooseTheme());
        // Calls b.rowListTheme.setOnClickListener with v -> chooseListStyle( to perform the operation shown.
        b.rowListTheme.setOnClickListener(v -> chooseListStyle());
        // Calls b.rowNotifications.setOnClickListener with v -> editNotificationPreferences( to perform the operation shown.
        b.rowNotifications.setOnClickListener(v -> editNotificationPreferences());
        // Calls b.rowExact.setOnClickListener with v -> openExactAlarmAccess( to perform the operation shown.
        b.rowExact.setOnClickListener(v -> openExactAlarmAccess());
        // Calls b.rowPrivacy.setOnClickListener with v -> new MaterialAlertDialogBuilder(this to perform the operation shown.
        b.rowPrivacy.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.setting_privacy)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(R.string.privacy_message)
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.ok, null).show());
        // Calls b.rowAbout.setOnClickListener with v -> new MaterialAlertDialogBuilder(this to perform the operation shown.
        b.rowAbout.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.setting_about)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(getString(R.string.about_message, versionName()))
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.ok, null).show());
        // Calls b.rowExit.setOnClickListener with v -> MainActivity.confirmExit(this to perform the operation shown.
        b.rowExit.setOnClickListener(v -> MainActivity.confirmExit(this));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    protected void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls refreshValues with  to perform the operation shown.
        refreshValues();
    // Closes the current method, control-flow, or type block.
    }

    // Refreshes values.
    private void refreshValues() {
        // Calls b.tvThemeValue.setText with themeLabels()[indexOf(THEME_KEYS, settings.get(Constants.SETTING_THEME, Constants.THEME_SYSTEM))] to perform the operation shown.
        b.tvThemeValue.setText(themeLabels()[indexOf(THEME_KEYS, settings.get(Constants.SETTING_THEME, Constants.THEME_SYSTEM))]);
        // Calls b.tvListValue.setText with listLabels()[indexOf(LIST_KEYS, settings.get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT))] to perform the operation shown.
        b.tvListValue.setText(listLabels()[indexOf(LIST_KEYS, settings.get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT))]);
        // If !NotificationHelper.canPost(this) is true, { immediately.
        if (!NotificationHelper.canPost(this)) {
            // Calls b.tvNotifValue.setText with R.string.notifications_blocked to perform the operation shown.
            b.tvNotifValue.setText(R.string.notifications_blocked);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls b.tvNotifValue.setText with settings.getBool(Constants.SETTING_NOTIF_ENABLED, true to perform the operation shown.
            b.tvNotifValue.setText(settings.getBool(Constants.SETTING_NOTIF_ENABLED, true)
                    // Executes ? R.string.on : R.string.off) as a statement.
                    ? R.string.on : R.string.off);
        // Closes the current method, control-flow, or type block.
        }
        // Calls b.tvExactValue.setText with Build.VERSION.SDK_INT < Build.VERSION_CODES.S ? R.string.exact_not_required to perform the operation shown.
        b.tvExactValue.setText(Build.VERSION.SDK_INT < Build.VERSION_CODES.S ? R.string.exact_not_required
                // Calls AlarmScheduler.canScheduleExact with this) ? R.string.exact_allowed : R.string.exact_not_allowed to perform the operation shown.
                : AlarmScheduler.canScheduleExact(this) ? R.string.exact_allowed : R.string.exact_not_allowed);
        // Calls b.tvPrivacyValue.setText with R.string.privacy_summary to perform the operation shown.
        b.tvPrivacyValue.setText(R.string.privacy_summary);
        // Calls b.tvAboutValue.setText with getString(R.string.about_summary, versionName( to perform the operation shown.
        b.tvAboutValue.setText(getString(R.string.about_summary, versionName()));
        // Calls b.tvExitValue.setText with R.string.exit_summary to perform the operation shown.
        b.tvExitValue.setText(R.string.exit_summary);
    // Closes the current method, control-flow, or type block.
    }

    // Declares themeLabels() to implement this part of the component.
    private String[] themeLabels() {
        // Returns new String[]{getString(R.string.theme_system), getString(R.string.theme_light), to the caller.
        return new String[]{getString(R.string.theme_system), getString(R.string.theme_light),
                // Calls getString with R.string.theme_dark), getString(R.string.theme_lavender)} to perform the operation shown.
                getString(R.string.theme_dark), getString(R.string.theme_lavender)};
    // Closes the current method, control-flow, or type block.
    }

    // Declares listLabels() to implement this part of the component.
    private String[] listLabels() {
        // Returns new String[]{getString(R.string.list_default), getString(R.string.list_compact), to the caller.
        return new String[]{getString(R.string.list_default), getString(R.string.list_compact),
                // Calls getString with R.string.list_detailed)} to perform the operation shown.
                getString(R.string.list_detailed)};
    // Closes the current method, control-flow, or type block.
    }

    // Declares indexOf() to implement this part of the component.
    private static int indexOf(String[] keys, String key) {
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < keys.length; i++) if (keys[i].equals(key).
        for (int i = 0; i < keys.length; i++) if (keys[i].equals(key)) return i;
        // Returns 0 to the caller.
        return 0;
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose theme.
    private void chooseTheme() {
        // Initializes current with indexOf(THEME_KEYS, settings.get(Constants.SETTING_THEME, Constants.THEME_SYSTEM)) so later statements can use the computed value.
        int current = indexOf(THEME_KEYS, settings.get(Constants.SETTING_THEME, Constants.THEME_SYSTEM));
        // Creates the object described by new MaterialAlertDialogBuilder(this).
        new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.setting_theme)
                // Starts the callback body that handles the event or asynchronous result.
                .setSingleChoiceItems(themeLabels(), current, (d, which) -> {
                    // Calls settings.put with Constants.SETTING_THEME, THEME_KEYS[which] to perform the operation shown.
                    settings.put(Constants.SETTING_THEME, THEME_KEYS[which]);
                    // Calls d.dismiss with  to perform the operation shown.
                    d.dismiss();
                    // Calls ThemeUtils.applyNightMode with this to perform the operation shown.
                    ThemeUtils.applyNightMode(this);
                    // Calls recreate with  to perform the operation shown.
                    recreate();
                // Continues the expression above with }).
                })
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose list style.
    private void chooseListStyle() {
        // Initializes current with indexOf(LIST_KEYS, settings.get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT)) so later statements can use the computed value.
        int current = indexOf(LIST_KEYS, settings.get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        // Creates the object described by new MaterialAlertDialogBuilder(this).
        new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.setting_list_theme)
                // Starts the callback body that handles the event or asynchronous result.
                .setSingleChoiceItems(listLabels(), current, (d, which) -> {
                    // Calls settings.put with Constants.SETTING_LIST_STYLE, LIST_KEYS[which] to perform the operation shown.
                    settings.put(Constants.SETTING_LIST_STYLE, LIST_KEYS[which]);
                    // Calls d.dismiss with  to perform the operation shown.
                    d.dismiss();
                    // Calls refreshValues with  to perform the operation shown.
                    refreshValues();
                    // Calls UiUtils.toast with this, R.string.setting_applied to perform the operation shown.
                    UiUtils.toast(this, R.string.setting_applied);
                // Continues the expression above with }).
                })
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    // Declares editNotificationPreferences() to implement this part of the component.
    private void editNotificationPreferences() {
        // Initializes keys with {Constants.SETTING_NOTIF_ENABLED, Constants.SETTING_NOTIF_TODO, so later statements can use the computed value.
        String[] keys = {Constants.SETTING_NOTIF_ENABLED, Constants.SETTING_NOTIF_TODO,
                // Executes Constants.SETTING_NOTIF_ALARM, Constants.SETTING_NOTE_HISTORY} as a statement.
                Constants.SETTING_NOTIF_ALARM, Constants.SETTING_NOTE_HISTORY};
        // Initializes labels with {getString(R.string.pref_notifications_all), getString(R.string.pref_todo_reminders), so later statements can use the computed value.
        String[] labels = {getString(R.string.pref_notifications_all), getString(R.string.pref_todo_reminders),
                // Calls getString with R.string.pref_alarm_alerts), getString(R.string.pref_note_history)} to perform the operation shown.
                getString(R.string.pref_alarm_alerts), getString(R.string.pref_note_history)};
        // Executes boolean[] checked = new boolean[keys.length] as a statement.
        boolean[] checked = new boolean[keys.length];
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < keys.length; i++) checked[i] = settings.getBool(keys[i], true.
        for (int i = 0; i < keys.length; i++) checked[i] = settings.getBool(keys[i], true);
        // Creates the object described by new MaterialAlertDialogBuilder(this).
        new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.setting_notifications)
                // Calls setMultiChoiceItems on the object from the preceding line with the shown arguments.
                .setMultiChoiceItems(labels, checked, (d, which, isChecked) -> checked[which] = isChecked)
                // Starts the callback body that handles the event or asynchronous result.
                .setPositiveButton(R.string.save, (d, w) -> {
                    // Repeats the following block according to the initialization, condition, and update in int i = 0; i < keys.length; i++) settings.putBool(keys[i], checked[i].
                    for (int i = 0; i < keys.length; i++) settings.putBool(keys[i], checked[i]);
                    // Calls refreshValues with  to perform the operation shown.
                    refreshValues();
                // Continues the expression above with }).
                })
                // Calls setNeutralButton on the object from the preceding line with the shown arguments.
                .setNeutralButton(R.string.system_settings, (d, w) -> startActivity(
                        // Creates the object described by new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).
                        new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                // Calls putExtra on the object from the preceding line with the shown arguments.
                                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())))
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    // Opens exact alarm access.
    private void openExactAlarmAccess() {
        // If Build.VERSION.SDK_INT >= Build.VERSION_CODES.S is true, { immediately.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Calls startActivity with new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, to perform the operation shown.
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    // Calls Uri.parse with "package:" + getPackageName( to perform the operation shown.
                    Uri.parse("package:" + getPackageName())));
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls UiUtils.toast with this, R.string.exact_not_required to perform the operation shown.
            UiUtils.toast(this, R.string.exact_not_required);
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares versionName() to implement this part of the component.
    private String versionName() {
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Initializes info with getPackageManager().getPackageInfo(getPackageName(), 0) so later statements can use the computed value.
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            // Returns info.versionName to the caller.
            return info.versionName;
        // Calls catch with Exception e) { to perform the operation shown.
        } catch (Exception e) {
            // Returns "1.0" to the caller.
            return "1.0";
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
