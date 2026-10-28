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

// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.utils.Repeat so this file can use that type by its short name.
import com.mit.notepad.utils.Repeat;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/**
 * Persists alarms and keeps AlarmManager in sync. Every alarm row has a unique id which is also
 * the PendingIntent request code, so one alarm can never schedule or cancel another.
 */
// Declares class AlarmRepository to group related state and behavior.
public class AlarmRepository {
    // Initializes OK with 0 so later statements can use the computed value.
    public static final int OK = 0;
    // Initializes PAST with 1 so later statements can use the computed value.
    public static final int PAST = 1;
    // Initializes NOT_FOUND with 2 so later statements can use the computed value.
    public static final int NOT_FOUND = 2;

    // Initializes SELECT_JOIN with  so later statements can use the computed value.
    private static final String SELECT_JOIN =
            // Calls COALESCE with n.title, t.title) AS owner_title, " to perform the operation shown.
            "SELECT a.*, COALESCE(n.title, t.title) AS owner_title, "
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "CASE WHEN a.todo_id IS NOT NULL OR n.note_type='TODO' THEN 1 ELSE 0 END AS is_task "
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "FROM alarms a LEFT JOIN notes n ON n.id=a.note_id LEFT JOIN todos t ON t.id=a.todo_id ";

    // Declares context for use by the surrounding method or class.
    private final Context context;
    // Declares helper for use by the surrounding method or class.
    private final NotepadDatabase helper;

    // Declares AlarmRepository() to implement this part of the component.
    public AlarmRepository(Context context) {
        // Delegates initialization to another constructor with context, NotepadDatabase.getInstance(context)).
        this(context, NotepadDatabase.getInstance(context));
    // Closes the current method, control-flow, or type block.
    }

    // Declares AlarmRepository() to implement this part of the component.
    public AlarmRepository(Context context, NotepadDatabase helper) {
        // Stores the result of context.getApplicationContext() in this.context for use by later logic.
        this.context = context.getApplicationContext();
        // Stores the result of helper in this.helper for use by later logic.
        this.helper = helper;
    // Closes the current method, control-flow, or type block.
    }

    // Copies the database columns into a model object.
    private static Alarm map(Cursor c) {
        // Initializes a with new Alarm() so later statements can use the computed value.
        Alarm a = new Alarm();
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("id")) in a.id for use by later logic.
        a.id = c.getLong(c.getColumnIndexOrThrow("id"));
        // Initializes noteIdx with the text or query fragment shown here.
        int noteIdx = c.getColumnIndexOrThrow("note_id");
        // Initializes todoIdx with the text or query fragment shown here.
        int todoIdx = c.getColumnIndexOrThrow("todo_id");
        // Stores the result of c.isNull(noteIdx) ? 0 : c.getLong(noteIdx) in a.noteId for use by later logic.
        a.noteId = c.isNull(noteIdx) ? 0 : c.getLong(noteIdx);
        // Stores the result of c.isNull(todoIdx) ? 0 : c.getLong(todoIdx) in a.todoId for use by later logic.
        a.todoId = c.isNull(todoIdx) ? 0 : c.getLong(todoIdx);
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("scheduled_at")) in a.scheduledAt for use by later logic.
        a.scheduledAt = c.getLong(c.getColumnIndexOrThrow("scheduled_at"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("is_enabled")) == 1 in a.enabled for use by later logic.
        a.enabled = c.getInt(c.getColumnIndexOrThrow("is_enabled")) == 1;
        // Stores the result of c.getString(c.getColumnIndexOrThrow("repeat_type")) in a.repeatType for use by later logic.
        a.repeatType = c.getString(c.getColumnIndexOrThrow("repeat_type"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("repeat_interval")) in a.repeatInterval for use by later logic.
        a.repeatInterval = c.getInt(c.getColumnIndexOrThrow("repeat_interval"));
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("created_at")) in a.createdAt for use by later logic.
        a.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        // Initializes title with the text or query fragment shown here.
        String title = c.getString(c.getColumnIndexOrThrow("owner_title"));
        // Stores the result of title == null ? "" : title in a.ownerTitle for use by later logic.
        a.ownerTitle = title == null ? "" : title;
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("is_task")) == 1 in a.isTask for use by later logic.
        a.isTask = c.getInt(c.getColumnIndexOrThrow("is_task")) == 1;
        // Returns a to the caller.
        return a;
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ queries

    // Returns the requested all.
    public List<Alarm> getAll() {
        // Initializes list with new ArrayList<>() so later statements can use the computed value.
        List<Alarm> list = new ArrayList<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Continues the expression above with SELECT_JOIN + "ORDER BY a.is_enabled DESC, a.scheduled_at ASC", null)) {.
                SELECT_JOIN + "ORDER BY a.is_enabled DESC, a.scheduled_at ASC", null)) {
            // If c.moveToNext() is true, list.add(map(c)) immediately.
            while (c.moveToNext()) list.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Returns list to the caller.
        return list;
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested enabled.
    public List<Alarm> getEnabled() {
        // Initializes list with new ArrayList<>() so later statements can use the computed value.
        List<Alarm> list = new ArrayList<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Continues the expression above with SELECT_JOIN + "WHERE a.is_enabled=1 ORDER BY a.scheduled_at ASC", null)) {.
                SELECT_JOIN + "WHERE a.is_enabled=1 ORDER BY a.scheduled_at ASC", null)) {
            // If c.moveToNext() is true, list.add(map(c)) immediately.
            while (c.moveToNext()) list.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Returns list to the caller.
        return list;
    // Closes the current method, control-flow, or type block.
    }

    // Declares get() to implement this part of the component.
    public Alarm get(long id) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls String.valueOf with id)})) { to perform the operation shown.
                SELECT_JOIN + "WHERE a.id=?", new String[]{String.valueOf(id)})) {
            // Returns c.moveToFirst() ? map(c) : null to the caller.
            return c.moveToFirst() ? map(c) : null;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested for note.
    public Alarm getForNote(long noteId) {
        // Returns findByOwner(helper.getReadableDatabase(), "note_id", noteId) to the caller.
        return findByOwner(helper.getReadableDatabase(), "note_id", noteId);
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested for todo.
    public Alarm getForTodo(long todoId) {
        // Returns findByOwner(helper.getReadableDatabase(), "todo_id", todoId) to the caller.
        return findByOwner(helper.getReadableDatabase(), "todo_id", todoId);
    // Closes the current method, control-flow, or type block.
    }

    /** ownerCol is always the constant "note_id" or "todo_id". */
    // Finds the requested by owner.
    static Alarm findByOwner(SQLiteDatabase db, String ownerCol, long ownerId) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = db.rawQuery(SELECT_JOIN + "WHERE a." + ownerCol + "=?",
                // Creates the object described by new String[]{String.valueOf(ownerId)})) {.
                new String[]{String.valueOf(ownerId)})) {
            // Returns c.moveToFirst() ? map(c) : null to the caller.
            return c.moveToFirst() ? map(c) : null;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ writes

    // Persists for note.
    public long saveForNote(long noteId, long at, boolean enabled, String repeat, int interval) {
        // Returns save("note_id", noteId, at, enabled, repeat, interval) to the caller.
        return save("note_id", noteId, at, enabled, repeat, interval);
    // Closes the current method, control-flow, or type block.
    }

    // Persists for todo.
    public long saveForTodo(long todoId, long at, boolean enabled, String repeat, int interval) {
        // Returns save("todo_id", todoId, at, enabled, repeat, interval) to the caller.
        return save("todo_id", todoId, at, enabled, repeat, interval);
    // Closes the current method, control-flow, or type block.
    }

    // Validates and persists the values currently entered in the form.
    private long save(String col, long ownerId, long at, boolean enabled, String repeat, int interval) {
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Declares saved for use by the surrounding method or class.
        Alarm saved;
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Stores the result of upsert(db, col, ownerId, at, enabled, repeat, interval) in saved for use by later logic.
            saved = upsert(db, col, ownerId, at, enabled, repeat, interval);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // Calls apply with saved to perform the operation shown.
        apply(saved);
        // Returns saved.id to the caller.
        return saved.id;
    // Closes the current method, control-flow, or type block.
    }

    /** Creates or updates the single alarm of an owner inside an open transaction. */
    // Declares upsert() to implement this part of the component.
    static Alarm upsert(SQLiteDatabase db, String col, long ownerId, long at, boolean enabled,
                        // Declares repeat for use by the surrounding method or class.
                        String repeat, int interval) {
        // Initializes existing with findByOwner(db, col, ownerId) so later statements can use the computed value.
        Alarm existing = findByOwner(db, col, ownerId);
        // Initializes v with new ContentValues() so later statements can use the computed value.
        ContentValues v = new ContentValues();
        // Calls v.put with "scheduled_at", at to perform the operation shown.
        v.put("scheduled_at", at);
        // Calls v.put with "is_enabled", enabled ? 1 : 0 to perform the operation shown.
        v.put("is_enabled", enabled ? 1 : 0);
        // Calls v.put with "repeat_type", repeat to perform the operation shown.
        v.put("repeat_type", repeat);
        // Calls v.put with "repeat_interval", Math.max(1, interval to perform the operation shown.
        v.put("repeat_interval", Math.max(1, interval));
        // If existing == null is true, { immediately.
        if (existing == null) {
            // Calls v.put with col, ownerId to perform the operation shown.
            v.put(col, ownerId);
            // Calls v.put with "created_at", System.currentTimeMillis( to perform the operation shown.
            v.put("created_at", System.currentTimeMillis());
            // Calls db.insertOrThrow with "alarms", null, v to perform the operation shown.
            db.insertOrThrow("alarms", null, v);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls db.update with "alarms", v, "id=?", new String[]{String.valueOf(existing.id)} to perform the operation shown.
            db.update("alarms", v, "id=?", new String[]{String.valueOf(existing.id)});
        // Closes the current method, control-flow, or type block.
        }
        // Initializes saved with findByOwner(db, col, ownerId) so later statements can use the computed value.
        Alarm saved = findByOwner(db, col, ownerId);
        // Calls syncTodo with db, saved to perform the operation shown.
        syncTodo(db, saved);
        // Returns saved to the caller.
        return saved;
    // Closes the current method, control-flow, or type block.
    }

    /** Deletes an owner's alarm inside an open transaction; returns the removed alarm id or -1. */
    // Deletes by owner.
    static long deleteByOwner(SQLiteDatabase db, String col, long ownerId) {
        // Initializes a with findByOwner(db, col, ownerId) so later statements can use the computed value.
        Alarm a = findByOwner(db, col, ownerId);
        // If a == null is true, return -1 immediately.
        if (a == null) return -1;
        // Calls db.delete with "alarms", "id=?", new String[]{String.valueOf(a.id)} to perform the operation shown.
        db.delete("alarms", "id=?", new String[]{String.valueOf(a.id)});
        // Calls db.execSQL with "UPDATE todos SET reminder_enabled=0 WHERE " to perform the operation shown.
        db.execSQL("UPDATE todos SET reminder_enabled=0 WHERE "
                // Adds ("note_id".equals(col) ? "note_id" : "id") + "=?", new Object[]{ownerId}) to the expression accumulated above.
                + ("note_id".equals(col) ? "note_id" : "id") + "=?", new Object[]{ownerId});
        // Returns a.id to the caller.
        return a.id;
    // Closes the current method, control-flow, or type block.
    }

    /** Mirrors alarm time/state into the owning to-do row (if any). */
    // Declares syncTodo() to implement this part of the component.
    private static void syncTodo(SQLiteDatabase db, Alarm a) {
        // Initializes where with the text or query fragment shown here.
        String where = a.noteId > 0 ? "note_id=?" : "id=?";
        // Initializes owner with a.noteId > 0 ? a.noteId : a.todoId so later statements can use the computed value.
        long owner = a.noteId > 0 ? a.noteId : a.todoId;
        // Calls db.execSQL with "UPDATE todos SET due_at=?, reminder_enabled=?, repeat_type=?, repeat_interval=? WHERE " + where, to perform the operation shown.
        db.execSQL("UPDATE todos SET due_at=?, reminder_enabled=?, repeat_type=?, repeat_interval=? WHERE " + where,
                // Creates the object described by new Object[]{a.scheduledAt, a.enabled ? 1 : 0, a.repeatType, a.repeatInterval, owner}).
                new Object[]{a.scheduledAt, a.enabled ? 1 : 0, a.repeatType, a.repeatInterval, owner});
    // Closes the current method, control-flow, or type block.
    }

    // Deletes for note.
    public void deleteForNote(long noteId) {
        // Calls deleteOwner with "note_id", noteId to perform the operation shown.
        deleteOwner("note_id", noteId);
    // Closes the current method, control-flow, or type block.
    }

    // Deletes for todo.
    public void deleteForTodo(long todoId) {
        // Calls deleteOwner with "todo_id", todoId to perform the operation shown.
        deleteOwner("todo_id", todoId);
    // Closes the current method, control-flow, or type block.
    }

    // Deletes owner.
    private void deleteOwner(String col, long ownerId) {
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Declares removed for use by the surrounding method or class.
        long removed;
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Stores the result of deleteByOwner(db, col, ownerId) in removed for use by later logic.
            removed = deleteByOwner(db, col, ownerId);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // If removed > 0 is true, AlarmScheduler.cancel(context, removed) immediately.
        if (removed > 0) AlarmScheduler.cancel(context, removed);
    // Closes the current method, control-flow, or type block.
    }

    // Declares delete() to implement this part of the component.
    public void delete(long alarmId) {
        // Initializes a with get(alarmId) so later statements can use the computed value.
        Alarm a = get(alarmId);
        // If a == null is true, return immediately.
        if (a == null) return;
        // If a.noteId > 0 is true, deleteForNote(a.noteId) immediately.
        if (a.noteId > 0) deleteForNote(a.noteId);
        // Runs the fallback branch when the preceding condition did not match.
        else deleteForTodo(a.todoId);
    // Closes the current method, control-flow, or type block.
    }

    /**
     * Enables/disables an alarm. Enabling a past one-off alarm is refused (PAST); a past repeating
     * alarm is moved to its next occurrence.
     */
    // Updates enabled.
    public int setEnabled(long id, boolean enabled) {
        // Initializes a with get(id) so later statements can use the computed value.
        Alarm a = get(id);
        // If a == null is true, return NOT_FOUND immediately.
        if (a == null) return NOT_FOUND;
        // Initializes at with a.scheduledAt so later statements can use the computed value.
        long at = a.scheduledAt;
        // Initializes now with System.currentTimeMillis() so later statements can use the computed value.
        long now = System.currentTimeMillis();
        // If enabled && at <= now is true, { immediately.
        if (enabled && at <= now) {
            // Initializes next with Repeat.nextOccurrence(at, a.repeatType, a.repeatInterval, now) so later statements can use the computed value.
            long next = Repeat.nextOccurrence(at, a.repeatType, a.repeatInterval, now);
            // If next < 0 is true, return PAST immediately.
            if (next < 0) return PAST;
            // Stores the result of next in at for use by later logic.
            at = next;
        // Closes the current method, control-flow, or type block.
        }
        // Stores the result of at in a.scheduledAt for use by later logic.
        a.scheduledAt = at;
        // Stores the result of enabled in a.enabled for use by later logic.
        a.enabled = enabled;
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls db.execSQL with "UPDATE alarms SET is_enabled=?, scheduled_at=? WHERE id=?", to perform the operation shown.
            db.execSQL("UPDATE alarms SET is_enabled=?, scheduled_at=? WHERE id=?",
                    // Creates the object described by new Object[]{enabled ? 1 : 0, at, id}).
                    new Object[]{enabled ? 1 : 0, at, id});
            // Calls syncTodo with db, a to perform the operation shown.
            syncTodo(db, a);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // Calls apply with a to perform the operation shown.
        apply(a);
        // Returns OK to the caller.
        return OK;
    // Closes the current method, control-flow, or type block.
    }

    /** Called after a repeating alarm fired: move it to its next occurrence and re-arm it. */
    // Declares reschedule() to implement this part of the component.
    public void reschedule(long id, long nextAt) {
        // Initializes a with get(id) so later statements can use the computed value.
        Alarm a = get(id);
        // If a == null is true, return immediately.
        if (a == null) return;
        // Stores the result of nextAt in a.scheduledAt for use by later logic.
        a.scheduledAt = nextAt;
        // Stores the result of true in a.enabled for use by later logic.
        a.enabled = true;
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls db.execSQL with "UPDATE alarms SET scheduled_at=?, is_enabled=1 WHERE id=?", new Object[]{nextAt, id} to perform the operation shown.
            db.execSQL("UPDATE alarms SET scheduled_at=?, is_enabled=1 WHERE id=?", new Object[]{nextAt, id});
            // Calls syncTodo with db, a to perform the operation shown.
            syncTodo(db, a);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // Calls apply with a to perform the operation shown.
        apply(a);
    // Closes the current method, control-flow, or type block.
    }

    // Declares disable() to implement this part of the component.
    public void disable(long id) {
        // Calls setEnabled with id, false to perform the operation shown.
        setEnabled(id, false);
    // Closes the current method, control-flow, or type block.
    }

    // Declares apply() to implement this part of the component.
    private void apply(Alarm a) {
        // If a.enabled is true, AlarmScheduler.schedule(context, a) immediately.
        if (a.enabled) AlarmScheduler.schedule(context, a);
        // Runs the fallback branch when the preceding condition did not match.
        else AlarmScheduler.cancel(context, a.id);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
