// Sets the package name to com.mit.notepad so this class belongs to the app module namespace.
package com.mit.notepad;

// Imports android.app.Application so this file can use that type by its short name.
import android.app.Application;

// Imports com.mit.notepad.notifications.NotificationHelper so this file can use that type by its short name.
import com.mit.notepad.notifications.NotificationHelper;
// Imports com.mit.notepad.utils.ThemeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.ThemeUtils;

// Declares class NotepadApp to group related state and behavior.
public class NotepadApp extends Application {
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    public void onCreate() {
        // Calls super.onCreate with  to perform the operation shown.
        super.onCreate();
        // Calls NotificationHelper.createChannels with this to perform the operation shown.
        NotificationHelper.createChannels(this);
        // Calls ThemeUtils.applyNightMode with this to perform the operation shown.
        ThemeUtils.applyNightMode(this);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
