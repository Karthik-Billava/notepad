// Sets the package name to com.mit.notepad.repositories so this class belongs to the app module namespace.
package com.mit.notepad.repositories;

// Imports android.content.ContentValues so this file can use that type by its short name.
import android.content.ContentValues;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.database.Cursor so this file can use that type by its short name.
import android.database.Cursor;
// Imports android.database.sqlite.SQLiteDatabase so this file can use that type by its short name.
import android.database.sqlite.SQLiteDatabase;

// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;

/** Key/value preferences persisted in the SQLite "settings" table. */
// Declares class SettingsRepository to group related state and behavior.
public class SettingsRepository {
    // Declares helper for use by the surrounding method or class.
    private final NotepadDatabase helper;

    // Updates tings repository.
    public SettingsRepository(Context context) {
        // Delegates initialization to another constructor with NotepadDatabase.getInstance(context)).
        this(NotepadDatabase.getInstance(context));
    // Closes the current method, control-flow, or type block.
    }

    // Updates tings repository.
    public SettingsRepository(NotepadDatabase helper) {
        // Stores the result of helper in this.helper for use by later logic.
        this.helper = helper;
    // Closes the current method, control-flow, or type block.
    }

    // Declares get() to implement this part of the component.
    public String get(String key, String defaultValue) {
        // Initializes db with helper.getReadableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getReadableDatabase();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = db.rawQuery("SELECT value FROM settings WHERE key=?", new String[]{key})) {
            // Returns c.moveToFirst() ? c.getString(0) : defaultValue to the caller.
            return c.moveToFirst() ? c.getString(0) : defaultValue;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares put() to implement this part of the component.
    public void put(String key, String value) {
        // Initializes v with new ContentValues() so later statements can use the computed value.
        ContentValues v = new ContentValues();
        // Calls v.put with "key", key to perform the operation shown.
        v.put("key", key);
        // Calls v.put with "value", value to perform the operation shown.
        v.put("value", value);
        // Calls helper.getWritableDatabase with ).insertWithOnConflict("settings", null, v, SQLiteDatabase.CONFLICT_REPLACE to perform the operation shown.
        helper.getWritableDatabase().insertWithOnConflict("settings", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested bool.
    public boolean getBool(String key, boolean defaultValue) {
        // Returns "1".equals(get(key, defaultValue ? "1" : "0")) to the caller.
        return "1".equals(get(key, defaultValue ? "1" : "0"));
    // Closes the current method, control-flow, or type block.
    }

    // Declares putBool() to implement this part of the component.
    public void putBool(String key, boolean value) {
        // Calls put with key, value ? "1" : "0" to perform the operation shown.
        put(key, value ? "1" : "0");
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
