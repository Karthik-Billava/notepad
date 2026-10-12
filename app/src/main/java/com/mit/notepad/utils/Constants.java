package com.mit.notepad.utils;

/** Application-wide constants. */
public final class Constants {
    private Constants() { }

    public static final int MAX_NOTES = 50;
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_TAG_NAME_LENGTH = 20;

    // Intent extras
    public static final String EXTRA_NOTE_ID = "extra_note_id";
    public static final String EXTRA_TODO_ID = "extra_todo_id";
    public static final String EXTRA_ALARM_ID = "extra_alarm_id";
    public static final String EXTRA_TAG_ID = "extra_tag_id";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final String EXTRA_RESULT_MODE = "extra_result_mode";
    public static final String EXTRA_ALARM_AT = "extra_alarm_at";
    public static final String EXTRA_ALARM_REPEAT = "extra_alarm_repeat";
    public static final String EXTRA_ALARM_INTERVAL = "extra_alarm_interval";
    public static final String EXTRA_ALARM_ENABLED = "extra_alarm_enabled";
    public static final String EXTRA_ALARM_REMOVE = "extra_alarm_remove";
    public static final String EXTRA_TARGET_LABEL = "extra_target_label";

    // Note types / task status / notification categories
    public static final String NOTE_TYPE_NOTE = "NOTE";
    public static final String NOTE_TYPE_TODO = "TODO";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String CATEGORY_TODO = "TODO";
    public static final String CATEGORY_ALARM = "ALARM";
    public static final String CATEGORY_NOTE = "NOTE";

    // Repeat types
    public static final String REPEAT_NONE = "NONE";
    public static final String REPEAT_DAILY = "DAILY";
    public static final String REPEAT_WEEKLY = "WEEKLY";
    public static final String REPEAT_CUSTOM = "CUSTOM";

    // Notification channels / actions
    public static final String CHANNEL_REMINDERS = "np_task_reminders";
    public static final String CHANNEL_ALARMS = "np_alarms";
    public static final String ACTION_ALARM = "com.mit.notepad.ACTION_ALARM";

    // Settings keys (stored in the SQLite "settings" table)
    public static final String SETTING_THEME = "theme";
    public static final String SETTING_LIST_STYLE = "list_style";
    public static final String SETTING_NOTIF_ENABLED = "notifications_enabled";
    public static final String SETTING_NOTIF_TODO = "todo_reminders_enabled";
    public static final String SETTING_NOTIF_ALARM = "alarm_alerts_enabled";
    public static final String SETTING_NOTE_HISTORY = "note_activity_history";
    public static final String SETTING_PERMISSION_ASKED = "notification_permission_asked";

    public static final String THEME_SYSTEM = "system";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_LAVENDER = "lavender";

    public static final String LIST_DEFAULT = "default";
    public static final String LIST_COMPACT = "compact";
    public static final String LIST_DETAILED = "detailed";
}
