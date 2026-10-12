package com.mit.notepad.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.utils.Repeat;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists alarms and keeps AlarmManager in sync. Every alarm row has a unique id which is also
 * the PendingIntent request code, so one alarm can never schedule or cancel another.
 */
public class AlarmRepository {
    public static final int OK = 0;
    public static final int PAST = 1;
    public static final int NOT_FOUND = 2;

    private static final String SELECT_JOIN =
            "SELECT a.*, COALESCE(n.title, t.title) AS owner_title, "
                    + "CASE WHEN a.todo_id IS NOT NULL OR n.note_type='TODO' THEN 1 ELSE 0 END AS is_task "
                    + "FROM alarms a LEFT JOIN notes n ON n.id=a.note_id LEFT JOIN todos t ON t.id=a.todo_id ";

    private final Context context;
    private final NotepadDatabase helper;

    public AlarmRepository(Context context) {
        this(context, NotepadDatabase.getInstance(context));
    }

    public AlarmRepository(Context context, NotepadDatabase helper) {
        this.context = context.getApplicationContext();
        this.helper = helper;
    }

    private static Alarm map(Cursor c) {
        Alarm a = new Alarm();
        a.id = c.getLong(c.getColumnIndexOrThrow("id"));
        int noteIdx = c.getColumnIndexOrThrow("note_id");
        int todoIdx = c.getColumnIndexOrThrow("todo_id");
        a.noteId = c.isNull(noteIdx) ? 0 : c.getLong(noteIdx);
        a.todoId = c.isNull(todoIdx) ? 0 : c.getLong(todoIdx);
        a.scheduledAt = c.getLong(c.getColumnIndexOrThrow("scheduled_at"));
        a.enabled = c.getInt(c.getColumnIndexOrThrow("is_enabled")) == 1;
        a.repeatType = c.getString(c.getColumnIndexOrThrow("repeat_type"));
        a.repeatInterval = c.getInt(c.getColumnIndexOrThrow("repeat_interval"));
        a.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        String title = c.getString(c.getColumnIndexOrThrow("owner_title"));
        a.ownerTitle = title == null ? "" : title;
        a.isTask = c.getInt(c.getColumnIndexOrThrow("is_task")) == 1;
        return a;
    }

    // ------------------------------------------------------------------ queries

    public List<Alarm> getAll() {
        List<Alarm> list = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                SELECT_JOIN + "ORDER BY a.is_enabled DESC, a.scheduled_at ASC", null)) {
            while (c.moveToNext()) list.add(map(c));
        }
        return list;
    }

    public List<Alarm> getEnabled() {
        List<Alarm> list = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                SELECT_JOIN + "WHERE a.is_enabled=1 ORDER BY a.scheduled_at ASC", null)) {
            while (c.moveToNext()) list.add(map(c));
        }
        return list;
    }

    public Alarm get(long id) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                SELECT_JOIN + "WHERE a.id=?", new String[]{String.valueOf(id)})) {
            return c.moveToFirst() ? map(c) : null;
        }
    }

    public Alarm getForNote(long noteId) {
        return findByOwner(helper.getReadableDatabase(), "note_id", noteId);
    }

    public Alarm getForTodo(long todoId) {
        return findByOwner(helper.getReadableDatabase(), "todo_id", todoId);
    }

    /** ownerCol is always the constant "note_id" or "todo_id". */
    static Alarm findByOwner(SQLiteDatabase db, String ownerCol, long ownerId) {
        try (Cursor c = db.rawQuery(SELECT_JOIN + "WHERE a." + ownerCol + "=?",
                new String[]{String.valueOf(ownerId)})) {
            return c.moveToFirst() ? map(c) : null;
        }
    }

    // ------------------------------------------------------------------ writes

    public long saveForNote(long noteId, long at, boolean enabled, String repeat, int interval) {
        return save("note_id", noteId, at, enabled, repeat, interval);
    }

    public long saveForTodo(long todoId, long at, boolean enabled, String repeat, int interval) {
        return save("todo_id", todoId, at, enabled, repeat, interval);
    }

    private long save(String col, long ownerId, long at, boolean enabled, String repeat, int interval) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Alarm saved;
        db.beginTransaction();
        try {
            saved = upsert(db, col, ownerId, at, enabled, repeat, interval);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        apply(saved);
        return saved.id;
    }

    /** Creates or updates the single alarm of an owner inside an open transaction. */
    static Alarm upsert(SQLiteDatabase db, String col, long ownerId, long at, boolean enabled,
                        String repeat, int interval) {
        Alarm existing = findByOwner(db, col, ownerId);
        ContentValues v = new ContentValues();
        v.put("scheduled_at", at);
        v.put("is_enabled", enabled ? 1 : 0);
        v.put("repeat_type", repeat);
        v.put("repeat_interval", Math.max(1, interval));
        if (existing == null) {
            v.put(col, ownerId);
            v.put("created_at", System.currentTimeMillis());
            db.insertOrThrow("alarms", null, v);
        } else {
            db.update("alarms", v, "id=?", new String[]{String.valueOf(existing.id)});
        }
        Alarm saved = findByOwner(db, col, ownerId);
        syncTodo(db, saved);
        return saved;
    }

    /** Deletes an owner's alarm inside an open transaction; returns the removed alarm id or -1. */
    static long deleteByOwner(SQLiteDatabase db, String col, long ownerId) {
        Alarm a = findByOwner(db, col, ownerId);
        if (a == null) return -1;
        db.delete("alarms", "id=?", new String[]{String.valueOf(a.id)});
        db.execSQL("UPDATE todos SET reminder_enabled=0 WHERE "
                + ("note_id".equals(col) ? "note_id" : "id") + "=?", new Object[]{ownerId});
        return a.id;
    }

    /** Mirrors alarm time/state into the owning to-do row (if any). */
    private static void syncTodo(SQLiteDatabase db, Alarm a) {
        String where = a.noteId > 0 ? "note_id=?" : "id=?";
        long owner = a.noteId > 0 ? a.noteId : a.todoId;
        db.execSQL("UPDATE todos SET due_at=?, reminder_enabled=?, repeat_type=?, repeat_interval=? WHERE " + where,
                new Object[]{a.scheduledAt, a.enabled ? 1 : 0, a.repeatType, a.repeatInterval, owner});
    }

    public void deleteForNote(long noteId) {
        deleteOwner("note_id", noteId);
    }

    public void deleteForTodo(long todoId) {
        deleteOwner("todo_id", todoId);
    }

    private void deleteOwner(String col, long ownerId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long removed;
        db.beginTransaction();
        try {
            removed = deleteByOwner(db, col, ownerId);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (removed > 0) AlarmScheduler.cancel(context, removed);
    }

    public void delete(long alarmId) {
        Alarm a = get(alarmId);
        if (a == null) return;
        if (a.noteId > 0) deleteForNote(a.noteId);
        else deleteForTodo(a.todoId);
    }

    /**
     * Enables/disables an alarm. Enabling a past one-off alarm is refused (PAST); a past repeating
     * alarm is moved to its next occurrence.
     */
    public int setEnabled(long id, boolean enabled) {
        Alarm a = get(id);
        if (a == null) return NOT_FOUND;
        long at = a.scheduledAt;
        long now = System.currentTimeMillis();
        if (enabled && at <= now) {
            long next = Repeat.nextOccurrence(at, a.repeatType, a.repeatInterval, now);
            if (next < 0) return PAST;
            at = next;
        }
        a.scheduledAt = at;
        a.enabled = enabled;
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE alarms SET is_enabled=?, scheduled_at=? WHERE id=?",
                    new Object[]{enabled ? 1 : 0, at, id});
            syncTodo(db, a);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        apply(a);
        return OK;
    }

    /** Called after a repeating alarm fired: move it to its next occurrence and re-arm it. */
    public void reschedule(long id, long nextAt) {
        Alarm a = get(id);
        if (a == null) return;
        a.scheduledAt = nextAt;
        a.enabled = true;
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE alarms SET scheduled_at=?, is_enabled=1 WHERE id=?", new Object[]{nextAt, id});
            syncTodo(db, a);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        apply(a);
    }

    public void disable(long id) {
        setEnabled(id, false);
    }

    private void apply(Alarm a) {
        if (a.enabled) AlarmScheduler.schedule(context, a);
        else AlarmScheduler.cancel(context, a.id);
    }
}
