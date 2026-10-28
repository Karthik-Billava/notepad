// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

/** Application-wide constants. */
// Declares class Constants to group related state and behavior.
public final class Constants {
    // Declares Constants() to implement this part of the component.
    private Constants() { }

    // Initializes MAX_NOTES with 50 so later statements can use the computed value.
    public static final int MAX_NOTES = 50;
    // Initializes MAX_TITLE_LENGTH with 100 so later statements can use the computed value.
    public static final int MAX_TITLE_LENGTH = 100;
    // Initializes MAX_DESCRIPTION_LENGTH with 500 so later statements can use the computed value.
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    // Initializes MAX_TAG_NAME_LENGTH with 20 so later statements can use the computed value.
    public static final int MAX_TAG_NAME_LENGTH = 20;

    // Intent extras
    // Initializes EXTRA_NOTE_ID with the text or query fragment shown here.
    public static final String EXTRA_NOTE_ID = "extra_note_id";
    // Initializes EXTRA_TODO_ID with the text or query fragment shown here.
    public static final String EXTRA_TODO_ID = "extra_todo_id";
    // Initializes EXTRA_ALARM_ID with the text or query fragment shown here.
    public static final String EXTRA_ALARM_ID = "extra_alarm_id";
    // Initializes EXTRA_TAG_ID with the text or query fragment shown here.
    public static final String EXTRA_TAG_ID = "extra_tag_id";
    // Initializes EXTRA_NOTIFICATION_ID with the text or query fragment shown here.
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    // Initializes EXTRA_RESULT_MODE with the text or query fragment shown here.
    public static final String EXTRA_RESULT_MODE = "extra_result_mode";
    // Initializes EXTRA_ALARM_AT with the text or query fragment shown here.
    public static final String EXTRA_ALARM_AT = "extra_alarm_at";
    // Initializes EXTRA_ALARM_REPEAT with the text or query fragment shown here.
    public static final String EXTRA_ALARM_REPEAT = "extra_alarm_repeat";
    // Initializes EXTRA_ALARM_INTERVAL with the text or query fragment shown here.
    public static final String EXTRA_ALARM_INTERVAL = "extra_alarm_interval";
    // Initializes EXTRA_ALARM_ENABLED with the text or query fragment shown here.
    public static final String EXTRA_ALARM_ENABLED = "extra_alarm_enabled";
    // Initializes EXTRA_ALARM_REMOVE with the text or query fragment shown here.
    public static final String EXTRA_ALARM_REMOVE = "extra_alarm_remove";
    // Initializes EXTRA_TARGET_LABEL with the text or query fragment shown here.
    public static final String EXTRA_TARGET_LABEL = "extra_target_label";

    // Note types / task status / notification categories
    // Initializes NOTE_TYPE_NOTE with the text or query fragment shown here.
    public static final String NOTE_TYPE_NOTE = "NOTE";
    // Initializes NOTE_TYPE_TODO with the text or query fragment shown here.
    public static final String NOTE_TYPE_TODO = "TODO";
    // Initializes STATUS_PENDING with the text or query fragment shown here.
    public static final String STATUS_PENDING = "PENDING";
    // Initializes STATUS_COMPLETED with the text or query fragment shown here.
    public static final String STATUS_COMPLETED = "COMPLETED";
    // Initializes CATEGORY_TODO with the text or query fragment shown here.
    public static final String CATEGORY_TODO = "TODO";
    // Initializes CATEGORY_ALARM with the text or query fragment shown here.
    public static final String CATEGORY_ALARM = "ALARM";
    // Initializes CATEGORY_NOTE with the text or query fragment shown here.
    public static final String CATEGORY_NOTE = "NOTE";

    // Repeat types
    // Initializes REPEAT_NONE with the text or query fragment shown here.
    public static final String REPEAT_NONE = "NONE";
    // Initializes REPEAT_DAILY with the text or query fragment shown here.
    public static final String REPEAT_DAILY = "DAILY";
    // Initializes REPEAT_WEEKLY with the text or query fragment shown here.
    public static final String REPEAT_WEEKLY = "WEEKLY";
    // Initializes REPEAT_CUSTOM with the text or query fragment shown here.
    public static final String REPEAT_CUSTOM = "CUSTOM";

    // Notification channels / actions
    // Initializes CHANNEL_REMINDERS with the text or query fragment shown here.
    public static final String CHANNEL_REMINDERS = "np_task_reminders";
    // Initializes CHANNEL_ALARMS with the text or query fragment shown here.
    public static final String CHANNEL_ALARMS = "np_alarms";
    // Initializes ACTION_ALARM with the text or query fragment shown here.
    public static final String ACTION_ALARM = "com.mit.notepad.ACTION_ALARM";

    // Settings keys (stored in the SQLite "settings" table)
    // Initializes SETTING_THEME with the text or query fragment shown here.
    public static final String SETTING_THEME = "theme";
    // Initializes SETTING_LIST_STYLE with the text or query fragment shown here.
    public static final String SETTING_LIST_STYLE = "list_style";
    // Initializes SETTING_NOTIF_ENABLED with the text or query fragment shown here.
    public static final String SETTING_NOTIF_ENABLED = "notifications_enabled";
    // Initializes SETTING_NOTIF_TODO with the text or query fragment shown here.
    public static final String SETTING_NOTIF_TODO = "todo_reminders_enabled";
    // Initializes SETTING_NOTIF_ALARM with the text or query fragment shown here.
    public static final String SETTING_NOTIF_ALARM = "alarm_alerts_enabled";
    // Initializes SETTING_NOTE_HISTORY with the text or query fragment shown here.
    public static final String SETTING_NOTE_HISTORY = "note_activity_history";
    // Initializes SETTING_PERMISSION_ASKED with the text or query fragment shown here.
    public static final String SETTING_PERMISSION_ASKED = "notification_permission_asked";

    // Initializes THEME_SYSTEM with the text or query fragment shown here.
    public static final String THEME_SYSTEM = "system";
    // Initializes THEME_LIGHT with the text or query fragment shown here.
    public static final String THEME_LIGHT = "light";
    // Initializes THEME_DARK with the text or query fragment shown here.
    public static final String THEME_DARK = "dark";
    // Initializes THEME_LAVENDER with the text or query fragment shown here.
    public static final String THEME_LAVENDER = "lavender";

    // Initializes LIST_DEFAULT with the text or query fragment shown here.
    public static final String LIST_DEFAULT = "default";
    // Initializes LIST_COMPACT with the text or query fragment shown here.
    public static final String LIST_COMPACT = "compact";
    // Initializes LIST_DETAILED with the text or query fragment shown here.
    public static final String LIST_DETAILED = "detailed";
// Closes the current method, control-flow, or type block.
}
