package com.mit.notepad.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.databinding.ActivitySettingsBinding;
import com.mit.notepad.notifications.NotificationHelper;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.ThemeUtils;
import com.mit.notepad.utils.UiUtils;

/** Screen 12: appearance, notification preferences, privacy, about and exit. */
public class SettingsActivity extends BaseActivity {
    private static final String[] THEME_KEYS = {Constants.THEME_SYSTEM, Constants.THEME_LIGHT,
            Constants.THEME_DARK, Constants.THEME_LAVENDER};
    private static final String[] LIST_KEYS = {Constants.LIST_DEFAULT, Constants.LIST_COMPACT,
            Constants.LIST_DETAILED};

    private ActivitySettingsBinding b;
    private SettingsRepository settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        settings = new SettingsRepository(this);
        setupBackHeader(b.header, R.string.settings, R.string.subtitle_settings);

        b.rowTheme.setOnClickListener(v -> chooseTheme());
        b.rowListTheme.setOnClickListener(v -> chooseListStyle());
        b.rowNotifications.setOnClickListener(v -> editNotificationPreferences());
        b.rowExact.setOnClickListener(v -> openExactAlarmAccess());
        b.rowPrivacy.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.setting_privacy)
                .setMessage(R.string.privacy_message)
                .setPositiveButton(R.string.ok, null).show());
        b.rowAbout.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.setting_about)
                .setMessage(getString(R.string.about_message, versionName()))
                .setPositiveButton(R.string.ok, null).show());
        b.rowExit.setOnClickListener(v -> MainActivity.confirmExit(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshValues();
    }

    private void refreshValues() {
        b.tvThemeValue.setText(themeLabels()[indexOf(THEME_KEYS, settings.get(Constants.SETTING_THEME, Constants.THEME_SYSTEM))]);
        b.tvListValue.setText(listLabels()[indexOf(LIST_KEYS, settings.get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT))]);
        if (!NotificationHelper.canPost(this)) {
            b.tvNotifValue.setText(R.string.notifications_blocked);
        } else {
            b.tvNotifValue.setText(settings.getBool(Constants.SETTING_NOTIF_ENABLED, true)
                    ? R.string.on : R.string.off);
        }
        b.tvExactValue.setText(Build.VERSION.SDK_INT < Build.VERSION_CODES.S ? R.string.exact_not_required
                : AlarmScheduler.canScheduleExact(this) ? R.string.exact_allowed : R.string.exact_not_allowed);
        b.tvPrivacyValue.setText(R.string.privacy_summary);
        b.tvAboutValue.setText(getString(R.string.about_summary, versionName()));
        b.tvExitValue.setText(R.string.exit_summary);
    }

    private String[] themeLabels() {
        return new String[]{getString(R.string.theme_system), getString(R.string.theme_light),
                getString(R.string.theme_dark), getString(R.string.theme_lavender)};
    }

    private String[] listLabels() {
        return new String[]{getString(R.string.list_default), getString(R.string.list_compact),
                getString(R.string.list_detailed)};
    }

    private static int indexOf(String[] keys, String key) {
        for (int i = 0; i < keys.length; i++) if (keys[i].equals(key)) return i;
        return 0;
    }

    private void chooseTheme() {
        int current = indexOf(THEME_KEYS, settings.get(Constants.SETTING_THEME, Constants.THEME_SYSTEM));
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.setting_theme)
                .setSingleChoiceItems(themeLabels(), current, (d, which) -> {
                    settings.put(Constants.SETTING_THEME, THEME_KEYS[which]);
                    d.dismiss();
                    ThemeUtils.applyNightMode(this);
                    recreate();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void chooseListStyle() {
        int current = indexOf(LIST_KEYS, settings.get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.setting_list_theme)
                .setSingleChoiceItems(listLabels(), current, (d, which) -> {
                    settings.put(Constants.SETTING_LIST_STYLE, LIST_KEYS[which]);
                    d.dismiss();
                    refreshValues();
                    UiUtils.toast(this, R.string.setting_applied);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void editNotificationPreferences() {
        String[] keys = {Constants.SETTING_NOTIF_ENABLED, Constants.SETTING_NOTIF_TODO,
                Constants.SETTING_NOTIF_ALARM, Constants.SETTING_NOTE_HISTORY};
        String[] labels = {getString(R.string.pref_notifications_all), getString(R.string.pref_todo_reminders),
                getString(R.string.pref_alarm_alerts), getString(R.string.pref_note_history)};
        boolean[] checked = new boolean[keys.length];
        for (int i = 0; i < keys.length; i++) checked[i] = settings.getBool(keys[i], true);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.setting_notifications)
                .setMultiChoiceItems(labels, checked, (d, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton(R.string.save, (d, w) -> {
                    for (int i = 0; i < keys.length; i++) settings.putBool(keys[i], checked[i]);
                    refreshValues();
                })
                .setNeutralButton(R.string.system_settings, (d, w) -> startActivity(
                        new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void openExactAlarmAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:" + getPackageName())));
        } else {
            UiUtils.toast(this, R.string.exact_not_required);
        }
    }

    private String versionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
}
