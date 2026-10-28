// Sets the package name to com.mit.notepad.database so this class belongs to the app module namespace.
package com.mit.notepad.database;

// Imports android.content.ContentValues so this file can use that type by its short name.
import android.content.ContentValues;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.database.sqlite.SQLiteDatabase so this file can use that type by its short name.
import android.database.sqlite.SQLiteDatabase;
// Imports android.database.sqlite.SQLiteOpenHelper so this file can use that type by its short name.
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
// Declares class NotepadDatabase to group related state and behavior.
public class NotepadDatabase extends SQLiteOpenHelper {
    // Initializes DB_NAME with the text or query fragment shown here.
    public static final String DB_NAME = "notepad.db";
    // Initializes DB_VERSION with 1 so later statements can use the computed value.
    public static final int DB_VERSION = 1;

    // Declares instance for use by the surrounding method or class.
    private static NotepadDatabase instance;

    // Returns the requested instance.
    public static synchronized NotepadDatabase getInstance(Context context) {
        // If instance == null is true, { immediately.
        if (instance == null) {
            // Stores the result of new NotepadDatabase(context.getApplicationContext(), DB_NAME) in instance for use by later logic.
            instance = new NotepadDatabase(context.getApplicationContext(), DB_NAME);
        // Closes the current method, control-flow, or type block.
        }
        // Returns instance to the caller.
        return instance;
    // Closes the current method, control-flow, or type block.
    }

    /** Public so tests can create isolated (or in-memory, name == null) databases. */
    // Declares NotepadDatabase() to implement this part of the component.
    public NotepadDatabase(Context context, String name) {
        // Initializes the parent class by passing context, name, null, DB_VERSION) to its constructor.
        super(context, name, null, DB_VERSION);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onConfigure() to implement this part of the component.
    public void onConfigure(SQLiteDatabase db) {
        // Calls super.onConfigure with db to perform the operation shown.
        super.onConfigure(db);
        // Calls db.setForeignKeyConstraintsEnabled with true to perform the operation shown.
        db.setForeignKeyConstraintsEnabled(true);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    public void onCreate(SQLiteDatabase db) {
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls db.execSQL with "CREATE TABLE notes (" to perform the operation shown.
            db.execSQL("CREATE TABLE notes ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "title TEXT NOT NULL CHECK(length(trim(title)) > 0),"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "content TEXT NOT NULL DEFAULT '',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "formatting TEXT NOT NULL DEFAULT '[]',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "created_at INTEGER NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "updated_at INTEGER NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "word_count INTEGER NOT NULL DEFAULT 0,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "character_count INTEGER NOT NULL DEFAULT 0,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "is_favorite INTEGER NOT NULL DEFAULT 0,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "is_pinned INTEGER NOT NULL DEFAULT 0,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "note_type TEXT NOT NULL DEFAULT 'NOTE' CHECK(note_type IN ('NOTE','TODO')))");

            // Calls db.execSQL with "CREATE TABLE tags (" to perform the operation shown.
            db.execSQL("CREATE TABLE tags ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "name TEXT NOT NULL COLLATE NOCASE UNIQUE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "color TEXT NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "icon TEXT NOT NULL DEFAULT 'label',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "last_used_at INTEGER NOT NULL DEFAULT 0)");

            // note_id is set for tasks created from a note; NULL for standalone tasks.
            // Calls db.execSQL with "CREATE TABLE todos (" to perform the operation shown.
            db.execSQL("CREATE TABLE todos ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "note_id INTEGER UNIQUE REFERENCES notes(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "title TEXT NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "description TEXT NOT NULL DEFAULT '',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','COMPLETED')),"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "due_at INTEGER,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "reminder_enabled INTEGER NOT NULL DEFAULT 0,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "repeat_type TEXT NOT NULL DEFAULT 'NONE',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "repeat_interval INTEGER NOT NULL DEFAULT 1,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "created_at INTEGER NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "completed_at INTEGER)");

            // Calls db.execSQL with "CREATE TABLE note_tags (" to perform the operation shown.
            db.execSQL("CREATE TABLE note_tags ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "note_id INTEGER NOT NULL REFERENCES notes(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "tag_id INTEGER NOT NULL REFERENCES tags(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "PRIMARY KEY(note_id, tag_id))");

            // Calls db.execSQL with "CREATE TABLE todo_tags (" to perform the operation shown.
            db.execSQL("CREATE TABLE todo_tags ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "todo_id INTEGER NOT NULL REFERENCES todos(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "tag_id INTEGER NOT NULL REFERENCES tags(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "PRIMARY KEY(todo_id, tag_id))");

            // Each alarm belongs to exactly one note OR one task.
            // Calls db.execSQL with "CREATE TABLE alarms (" to perform the operation shown.
            db.execSQL("CREATE TABLE alarms ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "note_id INTEGER REFERENCES notes(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "todo_id INTEGER REFERENCES todos(id) ON DELETE CASCADE,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "scheduled_at INTEGER NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "is_enabled INTEGER NOT NULL DEFAULT 1,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "repeat_type TEXT NOT NULL DEFAULT 'NONE',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "repeat_interval INTEGER NOT NULL DEFAULT 1,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "created_at INTEGER NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "CHECK((note_id IS NOT NULL AND todo_id IS NULL) OR (note_id IS NULL AND todo_id IS NOT NULL)))");

            // Calls db.execSQL with "CREATE TABLE notifications (" to perform the operation shown.
            db.execSQL("CREATE TABLE notifications ("
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "note_id INTEGER REFERENCES notes(id) ON DELETE SET NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "todo_id INTEGER REFERENCES todos(id) ON DELETE SET NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "title TEXT NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "message TEXT NOT NULL DEFAULT '',"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "timestamp INTEGER NOT NULL,"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "category TEXT NOT NULL CHECK(category IN ('TODO','ALARM','NOTE')),"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "is_read INTEGER NOT NULL DEFAULT 0)");

            // Calls db.execSQL with "CREATE TABLE settings (key TEXT PRIMARY KEY, value TEXT NOT NULL)" to perform the operation shown.
            db.execSQL("CREATE TABLE settings (key TEXT PRIMARY KEY, value TEXT NOT NULL)");

            // Indexes for search, filtering and relationships
            // Calls db.execSQL with "CREATE INDEX idx_notes_updated ON notes(updated_at)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_notes_updated ON notes(updated_at)");
            // Calls db.execSQL with "CREATE INDEX idx_notes_created ON notes(created_at)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_notes_created ON notes(created_at)");
            // Calls db.execSQL with "CREATE INDEX idx_notes_title ON notes(title COLLATE NOCASE)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_notes_title ON notes(title COLLATE NOCASE)");
            // Calls db.execSQL with "CREATE INDEX idx_notes_fav_pin ON notes(is_favorite, is_pinned)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_notes_fav_pin ON notes(is_favorite, is_pinned)");
            // Calls db.execSQL with "CREATE INDEX idx_todos_status_due ON todos(status, due_at)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_todos_status_due ON todos(status, due_at)");
            // Calls db.execSQL with "CREATE INDEX idx_note_tags_tag ON note_tags(tag_id)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_note_tags_tag ON note_tags(tag_id)");
            // Calls db.execSQL with "CREATE INDEX idx_todo_tags_tag ON todo_tags(tag_id)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_todo_tags_tag ON todo_tags(tag_id)");
            // Calls db.execSQL with "CREATE UNIQUE INDEX ux_alarms_note ON alarms(note_id) WHERE note_id IS NOT NULL" to perform the operation shown.
            db.execSQL("CREATE UNIQUE INDEX ux_alarms_note ON alarms(note_id) WHERE note_id IS NOT NULL");
            // Calls db.execSQL with "CREATE UNIQUE INDEX ux_alarms_todo ON alarms(todo_id) WHERE todo_id IS NOT NULL" to perform the operation shown.
            db.execSQL("CREATE UNIQUE INDEX ux_alarms_todo ON alarms(todo_id) WHERE todo_id IS NOT NULL");
            // Calls db.execSQL with "CREATE INDEX idx_alarms_time ON alarms(is_enabled, scheduled_at)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_alarms_time ON alarms(is_enabled, scheduled_at)");
            // Calls db.execSQL with "CREATE INDEX idx_notifications_time ON notifications(timestamp)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_notifications_time ON notifications(timestamp)");
            // Calls db.execSQL with "CREATE INDEX idx_notifications_cat ON notifications(category, is_read)" to perform the operation shown.
            db.execSQL("CREATE INDEX idx_notifications_cat ON notifications(category, is_read)");

            // Calls seedDefaultTags with db to perform the operation shown.
            seedDefaultTags(db);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares seedDefaultTags() to implement this part of the component.
    private void seedDefaultTags(SQLiteDatabase db) {
        // Initializes defaults with { so later statements can use the computed value.
        String[][] defaults = {
                // Supplies {"Study", "#7C6CF0", "school"} as the next argument to the call above.
                {"Study", "#7C6CF0", "school"},
                // Supplies {"Personal", "#F0476B", "heart"} as the next argument to the call above.
                {"Personal", "#F0476B", "heart"},
                // Supplies {"Work", "#F5B731", "work"} as the next argument to the call above.
                {"Work", "#F5B731", "work"},
                // Supplies {"Ideas", "#3B8EF0", "bulb"} as the next argument to the call above.
                {"Ideas", "#3B8EF0", "bulb"},
                // Supplies {"Important", "#3DBE7A", "star"} as the next argument to the call above.
                {"Important", "#3DBE7A", "star"},
                // Continues the expression above with {"Others", "#F28B50", "label"}.
                {"Others", "#F28B50", "label"}
        // Opens the block whose statements implement the declaration or branch above.
        };
        // Repeats the following block according to the initialization, condition, and update in String[] t : defaults.
        for (String[] t : defaults) {
            // Initializes v with new ContentValues() so later statements can use the computed value.
            ContentValues v = new ContentValues();
            // Calls v.put with "name", t[0] to perform the operation shown.
            v.put("name", t[0]);
            // Calls v.put with "color", t[1] to perform the operation shown.
            v.put("color", t[1]);
            // Calls v.put with "icon", t[2] to perform the operation shown.
            v.put("icon", t[2]);
            // Calls db.insert with "tags", null, v to perform the operation shown.
            db.insert("tags", null, v);
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /**
     * Safe, incremental migrations: data is preserved and each step only runs once.
     * Add a new "case" for every future schema version.
     */
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onUpgrade() to implement this part of the component.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Repeats the following block according to the initialization, condition, and update in int version = oldVersion; version < newVersion; version++.
        for (int version = oldVersion; version < newVersion; version++) {
            // Selects one branch according to the value of version.
            switch (version) {
                // case 1: // migrate 1 -> 2
                //     db.execSQL("ALTER TABLE notes ADD COLUMN ...");
                //     break;
                // Starts the fallback branch when no case value matches.
                default:
                    // Exits the innermost loop or switch statement.
                    break;
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
