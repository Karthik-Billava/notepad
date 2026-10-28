package com.mit.notepad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mit.notepad.utils.TextStats;

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
    public static final int DB_VERSION = 2;

    private static NotepadDatabase instance;
    private final boolean seedSampleData;

    public static synchronized NotepadDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new NotepadDatabase(context.getApplicationContext(), DB_NAME);
        }
        return instance;
    }

    /** Public so tests can create isolated (or in-memory, name == null) databases. */
    public NotepadDatabase(Context context, String name) {
        super(context, name, null, DB_VERSION);
        seedSampleData = DB_NAME.equals(name);
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
            if (seedSampleData) {
                seedSampleRecords(db);
            }
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

    /** Adds starter notes and tasks to the real app database, never to isolated test databases. */
    private void seedSampleRecords(SQLiteDatabase db) {
        long now = System.currentTimeMillis();

        long ideasTagId = findTagId(db, "Ideas");
        long studyTagId = findTagId(db, "Study");
        long personalTagId = findTagId(db, "Personal");

        long welcomeNoteId = insertSampleNote(db, "Welcome to Notepad",
                "Save ideas, plans, and reminders here. Edit or delete this sample whenever you like.",
                now, false, false);
        long studyNoteId = insertSampleNote(db, "Study plan",
                "Choose a topic, write down the key points, and review them before class.",
                now, false, false);
        long weeklyNoteId = insertSampleNote(db, "Ideas for the week",
                "Capture small ideas here so they are easy to find later.",
                now, true, true);

        linkNoteToTag(db, welcomeNoteId, ideasTagId);
        linkNoteToTag(db, studyNoteId, studyTagId);
        linkNoteToTag(db, weeklyNoteId, ideasTagId);

        long reviewTodoId = insertSampleTodo(db, "Review your notes",
                "Organise the ideas you want to keep.", now);
        long weeklyTodoId = insertSampleTodo(db, "Set a weekly goal",
                "Choose one task to finish this week.", now);
        linkTodoToTag(db, reviewTodoId, studyTagId);
        linkTodoToTag(db, weeklyTodoId, personalTagId);
    }

    private long insertSampleNote(SQLiteDatabase db, String title, String content, long now,
                                  boolean favorite, boolean pinned) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("formatting", "[]");
        values.put("created_at", now);
        values.put("updated_at", now);
        values.put("word_count", TextStats.wordCount(content));
        values.put("character_count", TextStats.charCount(content));
        values.put("is_favorite", favorite ? 1 : 0);
        values.put("is_pinned", pinned ? 1 : 0);
        values.put("note_type", "NOTE");
        return db.insertOrThrow("notes", null, values);
    }

    private long insertSampleTodo(SQLiteDatabase db, String title, String description, long now) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("status", "PENDING");
        values.put("created_at", now);
        return db.insertOrThrow("todos", null, values);
    }

    private long findTagId(SQLiteDatabase db, String name) {
        try (Cursor cursor = db.rawQuery("SELECT id FROM tags WHERE name = ?", new String[]{name})) {
            return cursor.moveToFirst() ? cursor.getLong(0) : -1;
        }
    }

    private void linkNoteToTag(SQLiteDatabase db, long noteId, long tagId) {
        if (tagId < 0) return;
        ContentValues values = new ContentValues();
        values.put("note_id", noteId);
        values.put("tag_id", tagId);
        db.insertOrThrow("note_tags", null, values);
    }

    private void linkTodoToTag(SQLiteDatabase db, long todoId, long tagId) {
        if (tagId < 0) return;
        ContentValues values = new ContentValues();
        values.put("todo_id", todoId);
        values.put("tag_id", tagId);
        db.insertOrThrow("todo_tags", null, values);
    }

    /**
     * Safe, incremental migrations: data is preserved and each step only runs once.
     * Add a new "case" for every future schema version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion; version < newVersion; version++) {
            switch (version) {
                case 1:
                    if (seedSampleData) {
                        seedSampleRecords(db);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
