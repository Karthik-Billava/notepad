package com.mit.notepad;

import android.app.Application;

import com.mit.notepad.notifications.NotificationHelper;
import com.mit.notepad.utils.ThemeUtils;

public class NotepadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createChannels(this);
        ThemeUtils.applyNightMode(this);
    }
}
