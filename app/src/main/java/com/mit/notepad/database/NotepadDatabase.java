package com.mit.notepad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite schema owner. The database is created once and upgraded in place; it is never wiped on start.
 *
 * Deletion behaviour:
 *  - deleting a note cascades to its linked to-do, its alarm and its tag links;
 *  - deleting a to-do cascades to its alarm and tag links;
 *  - deleting a tag only removes the links (notes/tasks are kept);
 *  - deleting a note/to-do keeps notification history (the reference is set to NULL).
 */
public class NotepadDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "notepad.db";
    public static final int DB_VERSION = 1;

    private static NotepadDatabase instance;

    public static synchronized NotepadDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new NotepadDatabase(context.getApplicationContext(), DB_NAME);
        }
        return instance;
    }

    /** Public so tests can create isolated (or in-memory, name == null) databases. */
    public NotepadDatabase(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE notes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title TEXT NOT NULL CHECK(length(trim(title)) > 0),"
                    + "content TEXT NOT NULL DEFAULT '',"
                    + "formatting TEXT NOT NULL DEFAULT '[]',"
                    + "created_at INTEGER NOT NULL,"
                    + "updated_at INTEGER NOT NULL,"
                    + "word_count INTEGER NOT NULL DEFAULT 0,"
                    + "character_count INTEGER NOT NULL DEFAULT 0,"
                    + "is_favorite INTEGER NOT NULL DEFAULT 0,"
                    + "is_pinned INTEGER NOT NULL DEFAULT 0,"
                    + "note_type TEXT NOT NULL DEFAULT 'NOTE' CHECK(note_type IN ('NOTE','TODO')))");

            db.execSQL("CREATE TABLE tags ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL COLLATE NOCASE UNIQUE,"
                    + "color TEXT NOT NULL,"
                    + "icon TEXT NOT NULL DEFAULT 'label',"
                    + "last_used_at INTEGER NOT NULL DEFAULT 0)");

            // note_id is set for tasks created from a note; NULL for standalone tasks.
            db.execSQL("CREATE TABLE todos ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "note_id INTEGER UNIQUE REFERENCES notes(id) ON DELETE CASCADE,"
                    + "title TEXT NOT NULL,"
                    + "description TEXT NOT NULL DEFAULT '',"
                    + "status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','COMPLETED')),"
                    + "due_at INTEGER,"
                    + "reminder_enabled INTEGER NOT NULL DEFAULT 0,"
                    + "repeat_type TEXT NOT NULL DEFAULT 'NONE',"
                    + "repeat_interval INTEGER NOT NULL DEFAULT 1,"
                    + "created_at INTEGER NOT NULL,"
                    + "completed_at INTEGER)");

            db.execSQL("CREATE TABLE note_tags ("
                    + "note_id INTEGER NOT NULL REFERENCES notes(id) ON DELETE CASCADE,"
                    + "tag_id INTEGER NOT NULL REFERENCES tags(id) ON DELETE CASCADE,"
                    + "PRIMARY KEY(note_id, tag_id))");

            db.execSQL("CREATE TABLE todo_tags ("
                    + "todo_id INTEGER NOT NULL REFERENCES todos(id) ON DELETE CASCADE,"
                    + "tag_id INTEGER NOT NULL REFERENCES tags(id) ON DELETE CASCADE,"
                    + "PRIMARY KEY(todo_id, tag_id))");

            // Each alarm belongs to exactly one note OR one task.
            db.execSQL("CREATE TABLE alarms ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "note_id INTEGER REFERENCES notes(id) ON DELETE CASCADE,"
                    + "todo_id INTEGER REFERENCES todos(id) ON DELETE CASCADE,"
                    + "scheduled_at INTEGER NOT NULL,"
                    + "is_enabled INTEGER NOT NULL DEFAULT 1,"
                    + "repeat_type TEXT NOT NULL DEFAULT 'NONE',"
                    + "repeat_interval INTEGER NOT NULL DEFAULT 1,"
                    + "created_at INTEGER NOT NULL,"
                    + "CHECK((note_id IS NOT NULL AND todo_id IS NULL) OR (note_id IS NULL AND todo_id IS NOT NULL)))");

            db.execSQL("CREATE TABLE notifications ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "note_id INTEGER REFERENCES notes(id) ON DELETE SET NULL,"
                    + "todo_id INTEGER REFERENCES todos(id) ON DELETE SET NULL,"
                    + "title TEXT NOT NULL,"
                    + "message TEXT NOT NULL DEFAULT '',"
                    + "timestamp INTEGER NOT NULL,"
                    + "category TEXT NOT NULL CHECK(category IN ('TODO','ALARM','NOTE')),"
                    + "is_read INTEGER NOT NULL DEFAULT 0)");

            db.execSQL("CREATE TABLE settings (key TEXT PRIMARY KEY, value TEXT NOT NULL)");

            // Indexes for search, filtering and relationships
            db.execSQL("CREATE INDEX idx_notes_updated ON notes(updated_at)");
            db.execSQL("CREATE INDEX idx_notes_created ON notes(created_at)");
            db.execSQL("CREATE INDEX idx_notes_title ON notes(title COLLATE NOCASE)");
            db.execSQL("CREATE INDEX idx_notes_fav_pin ON notes(is_favorite, is_pinned)");
            db.execSQL("CREATE INDEX idx_todos_status_due ON todos(status, due_at)");
            db.execSQL("CREATE INDEX idx_note_tags_tag ON note_tags(tag_id)");
            db.execSQL("CREATE INDEX idx_todo_tags_tag ON todo_tags(tag_id)");
            db.execSQL("CREATE UNIQUE INDEX ux_alarms_note ON alarms(note_id) WHERE note_id IS NOT NULL");
            db.execSQL("CREATE UNIQUE INDEX ux_alarms_todo ON alarms(todo_id) WHERE todo_id IS NOT NULL");
            db.execSQL("CREATE INDEX idx_alarms_time ON alarms(is_enabled, scheduled_at)");
            db.execSQL("CREATE INDEX idx_notifications_time ON notifications(timestamp)");
            db.execSQL("CREATE INDEX idx_notifications_cat ON notifications(category, is_read)");

            seedDefaultTags(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void seedDefaultTags(SQLiteDatabase db) {
        String[][] defaults = {
                {"Study", "#7C6CF0", "school"},
                {"Personal", "#F0476B", "heart"},
                {"Work", "#F5B731", "work"},
                {"Ideas", "#3B8EF0", "bulb"},
                {"Important", "#3DBE7A", "star"},
                {"Others", "#F28B50", "label"}
        };
        for (String[] t : defaults) {
            ContentValues v = new ContentValues();
            v.put("name", t[0]);
            v.put("color", t[1]);
            v.put("icon", t[2]);
            db.insert("tags", null, v);
        }
    }

    /**
     * Safe, incremental migrations: data is preserved and each step only runs once.
     * Add a new "case" for every future schema version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion; version < newVersion; version++) {
            switch (version) {
                // case 1: // migrate 1 -> 2
                //     db.execSQL("ALTER TABLE notes ADD COLUMN ...");
                //     break;
                default:
                    break;
            }
        }
    }
}
