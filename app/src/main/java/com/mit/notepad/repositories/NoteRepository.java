package com.mit.notepad.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mit.notepad.R;
import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.TextStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Data access and business rules for notes (including the 50-note limit). */
public class NoteRepository {
    public static final String SORT_RELEVANCE = "relevance";
    public static final String SORT_NEWEST = "newest";
    public static final String SORT_OLDEST = "oldest";
    public static final String SORT_TITLE = "title";

    public static class NoteLimitException extends Exception {
        public NoteLimitException() {
            super("Maximum of " + Constants.MAX_NOTES + " notes reached");
        }
    }

    /** Filter object for parameterised note queries. */
    public static class NoteQuery {
        public String keyword;
        public String noteType;
        public boolean favoritesOnly;
        public boolean pinnedOnly;
        public boolean pinnedFirst;
        public List<Long> tagIds = new ArrayList<>();
        /** Created/modified day range (0 = any). */
        public long dayStart;
        public long dayEnd;
        /** Created/modified hour of day 0-23 (-1 = any). */
        public int hour = -1;
        /** PENDING / COMPLETED for notes that are to-do items (null = any). */
        public String todoStatus;
        /** Due-date day range of the linked to-do (0 = any). */
        public long dueDayStart;
        public long dueDayEnd;
        public String sort = SORT_NEWEST;
    }

    private final Context context;
    private final NotepadDatabase helper;
    private final TagRepository tags;

    public NoteRepository(Context context) {
        this(context, NotepadDatabase.getInstance(context));
    }

    public NoteRepository(Context context, NotepadDatabase helper) {
        this.context = context.getApplicationContext();
        this.helper = helper;
        this.tags = new TagRepository(helper);
    }

    private static Note map(Cursor c) {
        Note n = new Note();
        n.id = c.getLong(c.getColumnIndexOrThrow("id"));
        n.title = c.getString(c.getColumnIndexOrThrow("title"));
        n.content = c.getString(c.getColumnIndexOrThrow("content"));
        n.formatting = c.getString(c.getColumnIndexOrThrow("formatting"));
        n.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        n.updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"));
        n.wordCount = c.getInt(c.getColumnIndexOrThrow("word_count"));
        n.charCount = c.getInt(c.getColumnIndexOrThrow("character_count"));
        n.favorite = c.getInt(c.getColumnIndexOrThrow("is_favorite")) == 1;
        n.pinned = c.getInt(c.getColumnIndexOrThrow("is_pinned")) == 1;
        n.noteType = c.getString(c.getColumnIndexOrThrow("note_type"));
        return n;
    }

    // ------------------------------------------------------------------ counts

    public int count() {
        return scalar("SELECT COUNT(*) FROM notes", null);
    }

    public int countByType(String type) {
        if (Constants.NOTE_TYPE_TODO.equals(type)) {
            return scalar("SELECT (SELECT COUNT(*) FROM notes WHERE note_type=?) + "
                    + "(SELECT COUNT(*) FROM todos WHERE note_id IS NULL)", new String[]{type});
        }
        return scalar("SELECT COUNT(*) FROM notes WHERE note_type=?", new String[]{type});
    }

    public int countStandalone() {
        return scalar("SELECT COUNT(*) FROM todos WHERE note_id IS NULL", null);
    }

    public int countFavorites() {
        return scalar("SELECT COUNT(*) FROM notes WHERE is_favorite=1", null);
    }

    public boolean isLimitReached() {
        return count() >= Constants.MAX_NOTES;
    }

    private int scalar(String sql, String[] args) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, args)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    // ------------------------------------------------------------------ reads

    public Note get(long id) {
        if (id < 0) {
            long todoId = -id;
            Todo t = new TodoRepository(context, helper).get(todoId);
            if (t == null) return null;
            Note n = new Note();
            n.id = id;
            n.title = t.title;
            n.content = t.description == null ? "" : t.description;
            n.formatting = "[]";
            n.noteType = Constants.NOTE_TYPE_TODO;
            n.createdAt = t.createdAt;
            n.updatedAt = t.completedAt > 0 ? t.completedAt : t.createdAt;
            n.wordCount = TextStats.wordCount(n.content);
            n.charCount = TextStats.charCount(n.content);
            n.favorite = false;
            n.pinned = false;
            n.tags = t.tags;
            return n;
        }
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT * FROM notes WHERE id=?", new String[]{String.valueOf(id)})) {
            if (!c.moveToFirst()) return null;
            Note n = map(c);
            n.tags = tags.getForNote(id);
            return n;
        }
    }

    public List<Note> getAll() {
        NoteQuery q = new NoteQuery();
        q.pinnedFirst = true;
        return query(q);
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /** Builds one parameterised query from the filter; no user text is concatenated into SQL. */
    public List<Note> query(NoteQuery q) {
        List<String> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT n.* FROM notes n WHERE 1=1");
        String kw = q.keyword == null ? "" : q.keyword.trim();
        String like = "%" + escapeLike(kw) + "%";

        if (!kw.isEmpty()) {
            sql.append(" AND (n.title LIKE ? ESCAPE '\\' OR n.content LIKE ? ESCAPE '\\')");
            args.add(like);
            args.add(like);
        }
        if (q.noteType != null) {
            sql.append(" AND n.note_type=?");
            args.add(q.noteType);
        }
        if (q.favoritesOnly) sql.append(" AND n.is_favorite=1");
        if (q.pinnedOnly) sql.append(" AND n.is_pinned=1");
        if (q.tagIds != null && !q.tagIds.isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM note_tags nt WHERE nt.note_id=n.id AND nt.tag_id IN (");
            for (int i = 0; i < q.tagIds.size(); i++) {
                sql.append(i == 0 ? "?" : ",?");
                args.add(String.valueOf(q.tagIds.get(i)));
            }
            sql.append("))");
        }
        if (q.dayStart > 0 || q.hour >= 0) {
            sql.append(" AND (");
            sql.append(dateTimeClause("created_at", q, args));
            sql.append(" OR ");
            sql.append(dateTimeClause("updated_at", q, args));
            sql.append(")");
        }
        if (q.todoStatus != null || q.dueDayStart > 0) {
            sql.append(" AND EXISTS (SELECT 1 FROM todos t WHERE t.note_id=n.id");
            if (q.todoStatus != null) {
                sql.append(" AND t.status=?");
                args.add(q.todoStatus);
            }
            if (q.dueDayStart > 0) {
                sql.append(" AND t.due_at BETWEEN ? AND ?");
                args.add(String.valueOf(q.dueDayStart));
                args.add(String.valueOf(q.dueDayEnd));
            }
            sql.append(")");
        }

        sql.append(" ORDER BY ");
        if (q.pinnedFirst) sql.append("n.is_pinned DESC, ");
        if (SORT_OLDEST.equals(q.sort)) {
            sql.append("n.created_at ASC");
        } else if (SORT_TITLE.equals(q.sort)) {
            sql.append("n.title COLLATE NOCASE ASC");
        } else if (SORT_RELEVANCE.equals(q.sort) && !kw.isEmpty()) {
            sql.append("CASE WHEN n.title LIKE ? ESCAPE '\\' THEN 0 ELSE 1 END, n.updated_at DESC");
            args.add(like);
        } else {
            sql.append("n.updated_at DESC");
        }

        List<Note> result = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (c.moveToNext()) result.add(map(c));
        }
        Map<Long, List<Tag>> tagMap = tags.noteTagMap();
        for (Note n : result) {
            List<Tag> t = tagMap.get(n.id);
            n.tags = t == null ? new ArrayList<Tag>() : t;
        }

        if (!q.favoritesOnly && !q.pinnedOnly && (q.noteType == null || Constants.NOTE_TYPE_TODO.equals(q.noteType))) {
            List<String> todoArgs = new ArrayList<>();
            StringBuilder todoSql = new StringBuilder("SELECT t.* FROM todos t WHERE t.note_id IS NULL");
            if (!kw.isEmpty()) {
                todoSql.append(" AND (t.title LIKE ? ESCAPE '\\' OR t.description LIKE ? ESCAPE '\\')");
                todoArgs.add(like);
                todoArgs.add(like);
            }
            if (q.tagIds != null && !q.tagIds.isEmpty()) {
                todoSql.append(" AND EXISTS (SELECT 1 FROM todo_tags tt WHERE tt.todo_id=t.id AND tt.tag_id IN (");
                for (int i = 0; i < q.tagIds.size(); i++) {
                    todoSql.append(i == 0 ? "?" : ",?");
                    todoArgs.add(String.valueOf(q.tagIds.get(i)));
                }
                todoSql.append("))");
            }
            if (q.todoStatus != null) {
                todoSql.append(" AND t.status=?");
                todoArgs.add(q.todoStatus);
            }
            if (q.dueDayStart > 0) {
                todoSql.append(" AND t.due_at BETWEEN ? AND ?");
                todoArgs.add(String.valueOf(q.dueDayStart));
                todoArgs.add(String.valueOf(q.dueDayEnd));
            }
            if (q.dayStart > 0) {
                todoSql.append(" AND t.created_at BETWEEN ? AND ?");
                todoArgs.add(String.valueOf(q.dayStart));
                todoArgs.add(String.valueOf(q.dueDayEnd > 0 ? q.dueDayEnd : q.dayEnd));
            }

            Map<Long, List<Tag>> todoTagMap = tags.todoTagMap();
            try (Cursor c = helper.getReadableDatabase().rawQuery(todoSql.toString(), todoArgs.toArray(new String[0]))) {
                while (c.moveToNext()) {
                    long todoId = c.getLong(c.getColumnIndexOrThrow("id"));
                    Note n = new Note();
                    n.id = -todoId;
                    n.title = c.getString(c.getColumnIndexOrThrow("title"));
                    n.content = c.getString(c.getColumnIndexOrThrow("description"));
                    n.formatting = "[]";
                    n.noteType = Constants.NOTE_TYPE_TODO;
                    n.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
                    int doneIdx = c.getColumnIndexOrThrow("completed_at");
                    long doneAt = c.isNull(doneIdx) ? 0 : c.getLong(doneIdx);
                    n.updatedAt = doneAt > 0 ? doneAt : n.createdAt;
                    n.wordCount = TextStats.wordCount(n.content);
                    n.charCount = TextStats.charCount(n.content);
                    n.favorite = false;
                    n.pinned = false;
                    List<Tag> tList = todoTagMap.get(todoId);
                    n.tags = tList == null ? new ArrayList<Tag>() : tList;
                    result.add(n);
                }
            }

            java.util.Collections.sort(result, (n1, n2) -> {
                if (q.pinnedFirst && n1.pinned != n2.pinned) {
                    return n1.pinned ? -1 : 1;
                }
                if (SORT_OLDEST.equals(q.sort)) {
                    return Long.compare(n1.createdAt, n2.createdAt);
                } else if (SORT_TITLE.equals(q.sort)) {
                    return n1.title.compareToIgnoreCase(n2.title);
                } else if (SORT_RELEVANCE.equals(q.sort) && !kw.isEmpty()) {
                    boolean m1 = n1.title.toLowerCase(Locale.getDefault()).contains(kw.toLowerCase(Locale.getDefault()));
                    boolean m2 = n2.title.toLowerCase(Locale.getDefault()).contains(kw.toLowerCase(Locale.getDefault()));
                    if (m1 != m2) return m1 ? -1 : 1;
                    return Long.compare(n2.updatedAt, n1.updatedAt);
                } else {
                    return Long.compare(n2.updatedAt, n1.updatedAt);
                }
            });
        }

        return result;
    }

    /** Date and/or hour condition on one timestamp column (column name is an internal constant). */
    private String dateTimeClause(String column, NoteQuery q, List<String> args) {
        StringBuilder sb = new StringBuilder("(1=1");
        if (q.dayStart > 0) {
            sb.append(" AND n.").append(column).append(" BETWEEN ? AND ?");
            args.add(String.valueOf(q.dayStart));
            args.add(String.valueOf(q.dayEnd));
        }
        if (q.hour >= 0) {
            sb.append(" AND strftime('%H', n.").append(column).append("/1000, 'unixepoch', 'localtime') = ?");
            args.add(String.format(Locale.US, "%02d", q.hour));
        }
        return sb.append(")").toString();
    }

    // ------------------------------------------------------------------ writes

    /**
     * Creates (id == 0) or updates a note and replaces its tags, in a single transaction.
     * Word/character counts and timestamps are computed here so they are always consistent.
     *
     * @return the note id
     * @throws NoteLimitException when creating a note while 50 notes already exist
     */
    public long save(Note n, List<Long> tagIds) throws NoteLimitException {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean isNew = n.id == 0;
        long now = System.currentTimeMillis();
        db.beginTransaction();
        try {
            n.title = n.title.trim();
            n.wordCount = TextStats.wordCount(n.content);
            n.charCount = TextStats.charCount(n.content);
            n.updatedAt = now;

            ContentValues v = new ContentValues();
            v.put("title", n.title);
            v.put("content", n.content);
            v.put("formatting", n.formatting);
            v.put("updated_at", now);
            v.put("word_count", n.wordCount);
            v.put("character_count", n.charCount);
            v.put("is_favorite", n.favorite ? 1 : 0);
            v.put("is_pinned", n.pinned ? 1 : 0);
            v.put("note_type", n.noteType);

            if (isNew) {
                if (scalarIn(db, "SELECT COUNT(*) FROM notes") >= Constants.MAX_NOTES) {
                    throw new NoteLimitException();
                }
                n.createdAt = now;
                v.put("created_at", now);
                n.id = db.insertOrThrow("notes", null, v);
            } else {
                int rows = db.update("notes", v, "id=?", new String[]{String.valueOf(n.id)});
                if (rows == 0) throw new IllegalStateException("Note " + n.id + " no longer exists");
            }
            TagRepository.replaceNoteTags(db, n.id, tagIds);
            syncTodo(db, n);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        recordEvent(isNew ? R.string.notif_note_created : R.string.notif_note_updated, n.title, n.id);
        return n.id;
    }

    private static int scalarIn(SQLiteDatabase db, String sql) {
        try (Cursor c = db.rawQuery(sql, null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    /** Keeps the linked to-do row in step with the note (create, update or remove). */
    private void syncTodo(SQLiteDatabase db, Note n) {
        long todoId = -1;
        try (Cursor c = db.rawQuery("SELECT id FROM todos WHERE note_id=?", new String[]{String.valueOf(n.id)})) {
            if (c.moveToFirst()) todoId = c.getLong(0);
        }
        if (n.isTodo()) {
            ContentValues v = new ContentValues();
            v.put("title", n.title);
            v.put("description", n.content.length() > Constants.MAX_DESCRIPTION_LENGTH
                    ? n.content.substring(0, Constants.MAX_DESCRIPTION_LENGTH) : n.content);
            if (todoId < 0) {
                v.put("note_id", n.id);
                v.put("status", Constants.STATUS_PENDING);
                v.put("created_at", System.currentTimeMillis());
                try (Cursor a = db.rawQuery("SELECT scheduled_at, is_enabled, repeat_type, repeat_interval "
                        + "FROM alarms WHERE note_id=?", new String[]{String.valueOf(n.id)})) {
                    if (a.moveToFirst()) {
                        v.put("due_at", a.getLong(0));
                        v.put("reminder_enabled", a.getInt(1));
                        v.put("repeat_type", a.getString(2));
                        v.put("repeat_interval", a.getInt(3));
                    }
                }
                db.insertOrThrow("todos", null, v);
            } else {
                db.update("todos", v, "id=?", new String[]{String.valueOf(todoId)});
            }
        } else if (todoId >= 0) {
            db.delete("todos", "id=?", new String[]{String.valueOf(todoId)});
        }
    }

    public void setFavorite(long id, boolean favorite) {
        helper.getWritableDatabase().execSQL("UPDATE notes SET is_favorite=? WHERE id=?",
                new Object[]{favorite ? 1 : 0, id});
    }

    public void setPinned(long id, boolean pinned) {
        helper.getWritableDatabase().execSQL("UPDATE notes SET is_pinned=? WHERE id=?",
                new Object[]{pinned ? 1 : 0, id});
    }

    /** Marks a note as a to-do item (or back to a plain note) and syncs the linked task. */
    public void setNoteType(long id, String type) {
        Note n = get(id);
        if (n == null) return;
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            n.noteType = type;
            db.execSQL("UPDATE notes SET note_type=?, updated_at=? WHERE id=?",
                    new Object[]{type, System.currentTimeMillis(), id});
            syncTodo(db, n);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void setNoteTags(long id, List<Long> tagIds) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            TagRepository.replaceNoteTags(db, id, tagIds);
            db.execSQL("UPDATE notes SET updated_at=? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /** Deletes a note, its linked task, tag links and alarm (cancelling the system alarm). */
    public void delete(long id) {
        Note n = get(id);
        if (n == null) return;
        SQLiteDatabase db = helper.getWritableDatabase();
        long alarmId = -1;
        db.beginTransaction();
        try {
            try (Cursor c = db.rawQuery("SELECT id FROM alarms WHERE note_id=?", new String[]{String.valueOf(id)})) {
                if (c.moveToFirst()) alarmId = c.getLong(0);
            }
            db.delete("notes", "id=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (alarmId > 0) AlarmScheduler.cancel(context, alarmId);
        recordEvent(R.string.notif_note_deleted, n.title, 0);
    }

    private void recordEvent(int titleRes, String noteTitle, long noteId) {
        if (!new SettingsRepository(helper).getBool(Constants.SETTING_NOTE_HISTORY, true)) return;
        new NotificationRepository(helper).add(context.getString(titleRes), noteTitle,
                Constants.CATEGORY_NOTE, noteId, 0);
    }
}
