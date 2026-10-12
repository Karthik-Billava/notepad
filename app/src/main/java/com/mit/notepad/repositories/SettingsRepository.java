package com.mit.notepad.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mit.notepad.database.NotepadDatabase;

/** Key/value preferences persisted in the SQLite "settings" table. */
public class SettingsRepository {
    private final NotepadDatabase helper;

    public SettingsRepository(Context context) {
        this(NotepadDatabase.getInstance(context));
    }

    public SettingsRepository(NotepadDatabase helper) {
        this.helper = helper;
    }

    public String get(String key, String defaultValue) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT value FROM settings WHERE key=?", new String[]{key})) {
            return c.moveToFirst() ? c.getString(0) : defaultValue;
        }
    }

    public void put(String key, String value) {
        ContentValues v = new ContentValues();
        v.put("key", key);
        v.put("value", value);
        helper.getWritableDatabase().insertWithOnConflict("settings", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean getBool(String key, boolean defaultValue) {
        return "1".equals(get(key, defaultValue ? "1" : "0"));
    }

    public void putBool(String key, boolean value) {
        put(key, value ? "1" : "0");
    }
}
