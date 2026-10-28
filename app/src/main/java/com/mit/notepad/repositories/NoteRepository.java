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

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.TextStats so this file can use that type by its short name.
import com.mit.notepad.utils.TextStats;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Locale so this file can use that type by its short name.
import java.util.Locale;
// Imports java.util.Map so this file can use that type by its short name.
import java.util.Map;

/** Data access and business rules for notes (including the 50-note limit). */
// Declares class NoteRepository to group related state and behavior.
public class NoteRepository {
    // Initializes SORT_RELEVANCE with the text or query fragment shown here.
    public static final String SORT_RELEVANCE = "relevance";
    // Initializes SORT_NEWEST with the text or query fragment shown here.
    public static final String SORT_NEWEST = "newest";
    // Initializes SORT_OLDEST with the text or query fragment shown here.
    public static final String SORT_OLDEST = "oldest";
    // Initializes SORT_TITLE with the text or query fragment shown here.
    public static final String SORT_TITLE = "title";

    // Declares class NoteLimitException to group related state and behavior.
    public static class NoteLimitException extends Exception {
        // Declares NoteLimitException() to implement this part of the component.
        public NoteLimitException() {
            // Initializes the parent class by passing "Maximum of " + Constants.MAX_NOTES + " notes reached") to its constructor.
            super("Maximum of " + Constants.MAX_NOTES + " notes reached");
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** Filter object for parameterised note queries. */
    // Declares class NoteQuery to group related state and behavior.
    public static class NoteQuery {
        // Declares keyword for use by the surrounding method or class.
        public String keyword;
        // Declares noteType for use by the surrounding method or class.
        public String noteType;
        // Declares favoritesOnly for use by the surrounding method or class.
        public boolean favoritesOnly;
        // Declares pinnedOnly for use by the surrounding method or class.
        public boolean pinnedOnly;
        // Declares pinnedFirst for use by the surrounding method or class.
        public boolean pinnedFirst;
        // Initializes tagIds with new ArrayList<>() so later statements can use the computed value.
        public List<Long> tagIds = new ArrayList<>();
        /** Created/modified day range (0 = any). */
        // Declares dayStart for use by the surrounding method or class.
        public long dayStart;
        // Declares dayEnd for use by the surrounding method or class.
        public long dayEnd;
        /** Created/modified hour of day 0-23 (-1 = any). */
        // Initializes hour with -1 so later statements can use the computed value.
        public int hour = -1;
        /** PENDING / COMPLETED for notes that are to-do items (null = any). */
        // Declares todoStatus for use by the surrounding method or class.
        public String todoStatus;
        /** Due-date day range of the linked to-do (0 = any). */
        // Declares dueDayStart for use by the surrounding method or class.
        public long dueDayStart;
        // Declares dueDayEnd for use by the surrounding method or class.
        public long dueDayEnd;
        // Initializes sort with SORT_NEWEST so later statements can use the computed value.
        public String sort = SORT_NEWEST;
    // Closes the current method, control-flow, or type block.
    }

    // Declares context for use by the surrounding method or class.
    private final Context context;
    // Declares helper for use by the surrounding method or class.
    private final NotepadDatabase helper;
    // Declares tags for use by the surrounding method or class.
    private final TagRepository tags;

    // Declares NoteRepository() to implement this part of the component.
    public NoteRepository(Context context) {
        // Delegates initialization to another constructor with context, NotepadDatabase.getInstance(context)).
        this(context, NotepadDatabase.getInstance(context));
    // Closes the current method, control-flow, or type block.
    }

    // Declares NoteRepository() to implement this part of the component.
    public NoteRepository(Context context, NotepadDatabase helper) {
        // Stores the result of context.getApplicationContext() in this.context for use by later logic.
        this.context = context.getApplicationContext();
        // Stores the result of helper in this.helper for use by later logic.
        this.helper = helper;
        // Stores the result of new TagRepository(helper) in this.tags for use by later logic.
        this.tags = new TagRepository(helper);
    // Closes the current method, control-flow, or type block.
    }

    // Copies the database columns into a model object.
    private static Note map(Cursor c) {
        // Initializes n with new Note() so later statements can use the computed value.
        Note n = new Note();
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("id")) in n.id for use by later logic.
        n.id = c.getLong(c.getColumnIndexOrThrow("id"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("title")) in n.title for use by later logic.
        n.title = c.getString(c.getColumnIndexOrThrow("title"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("content")) in n.content for use by later logic.
        n.content = c.getString(c.getColumnIndexOrThrow("content"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("formatting")) in n.formatting for use by later logic.
        n.formatting = c.getString(c.getColumnIndexOrThrow("formatting"));
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("created_at")) in n.createdAt for use by later logic.
        n.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("updated_at")) in n.updatedAt for use by later logic.
        n.updatedAt = c.getLong(c.getColumnIndexOrThrow("updated_at"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("word_count")) in n.wordCount for use by later logic.
        n.wordCount = c.getInt(c.getColumnIndexOrThrow("word_count"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("character_count")) in n.charCount for use by later logic.
        n.charCount = c.getInt(c.getColumnIndexOrThrow("character_count"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("is_favorite")) == 1 in n.favorite for use by later logic.
        n.favorite = c.getInt(c.getColumnIndexOrThrow("is_favorite")) == 1;
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("is_pinned")) == 1 in n.pinned for use by later logic.
        n.pinned = c.getInt(c.getColumnIndexOrThrow("is_pinned")) == 1;
        // Stores the result of c.getString(c.getColumnIndexOrThrow("note_type")) in n.noteType for use by later logic.
        n.noteType = c.getString(c.getColumnIndexOrThrow("note_type"));
        // Returns n to the caller.
        return n;
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ counts

    // Declares count() to implement this part of the component.
    public int count() {
        // Returns scalar("SELECT COUNT(*) FROM notes", null) to the caller.
        return scalar("SELECT COUNT(*) FROM notes", null);
    // Closes the current method, control-flow, or type block.
    }

    // Counts by type.
    public int countByType(String type) {
        // If Constants.NOTE_TYPE_TODO.equals(type) is true, { immediately.
        if (Constants.NOTE_TYPE_TODO.equals(type)) {
            // Returns scalar("SELECT (SELECT COUNT(*) FROM notes WHERE note_type=?) + " to the caller.
            return scalar("SELECT (SELECT COUNT(*) FROM notes WHERE note_type=?) + "
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "(SELECT COUNT(*) FROM todos WHERE note_id IS NULL)", new String[]{type});
        // Closes the current method, control-flow, or type block.
        }
        // Returns scalar("SELECT COUNT(*) FROM notes WHERE note_type=?", new String[]{type}) to the caller.
        return scalar("SELECT COUNT(*) FROM notes WHERE note_type=?", new String[]{type});
    // Closes the current method, control-flow, or type block.
    }

    // Counts standalone.
    public int countStandalone() {
        // Returns scalar("SELECT COUNT(*) FROM todos WHERE note_id IS NULL", null) to the caller.
        return scalar("SELECT COUNT(*) FROM todos WHERE note_id IS NULL", null);
    // Closes the current method, control-flow, or type block.
    }

    // Counts favorites.
    public int countFavorites() {
        // Returns scalar("SELECT COUNT(*) FROM notes WHERE is_favorite=1", null) to the caller.
        return scalar("SELECT COUNT(*) FROM notes WHERE is_favorite=1", null);
    // Closes the current method, control-flow, or type block.
    }

    // Checks whether limit reached.
    public boolean isLimitReached() {
        // Returns count() >= Constants.MAX_NOTES to the caller.
        return count() >= Constants.MAX_NOTES;
    // Closes the current method, control-flow, or type block.
    }

    // Declares scalar() to implement this part of the component.
    private int scalar(String sql, String[] args) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql, args)) {
            // Returns c.moveToFirst() ? c.getInt(0) : 0 to the caller.
            return c.moveToFirst() ? c.getInt(0) : 0;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ reads

    // Declares get() to implement this part of the component.
    public Note get(long id) {
        // If id < 0 is true, { immediately.
        if (id < 0) {
            // Initializes todoId with -id so later statements can use the computed value.
            long todoId = -id;
            // Initializes t with new TodoRepository(context, helper).get(todoId) so later statements can use the computed value.
            Todo t = new TodoRepository(context, helper).get(todoId);
            // If t == null is true, return null immediately.
            if (t == null) return null;
            // Initializes n with new Note() so later statements can use the computed value.
            Note n = new Note();
            // Stores the result of id in n.id for use by later logic.
            n.id = id;
            // Stores the result of t.title in n.title for use by later logic.
            n.title = t.title;
            // Stores the result of t.description == null ? "" : t.description in n.content for use by later logic.
            n.content = t.description == null ? "" : t.description;
            // Stores the result of "[]" in n.formatting for use by later logic.
            n.formatting = "[]";
            // Stores the result of Constants.NOTE_TYPE_TODO in n.noteType for use by later logic.
            n.noteType = Constants.NOTE_TYPE_TODO;
            // Stores the result of t.createdAt in n.createdAt for use by later logic.
            n.createdAt = t.createdAt;
            // Stores the result of t.completedAt > 0 ? t.completedAt : t.createdAt in n.updatedAt for use by later logic.
            n.updatedAt = t.completedAt > 0 ? t.completedAt : t.createdAt;
            // Stores the result of TextStats.wordCount(n.content) in n.wordCount for use by later logic.
            n.wordCount = TextStats.wordCount(n.content);
            // Stores the result of TextStats.charCount(n.content) in n.charCount for use by later logic.
            n.charCount = TextStats.charCount(n.content);
            // Stores the result of false in n.favorite for use by later logic.
            n.favorite = false;
            // Stores the result of false in n.pinned for use by later logic.
            n.pinned = false;
            // Stores the result of t.tags in n.tags for use by later logic.
            n.tags = t.tags;
            // Returns n to the caller.
            return n;
        // Closes the current method, control-flow, or type block.
        }
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls String.valueOf with id)})) { to perform the operation shown.
                "SELECT * FROM notes WHERE id=?", new String[]{String.valueOf(id)})) {
            // If !c.moveToFirst() is true, return null immediately.
            if (!c.moveToFirst()) return null;
            // Initializes n with map(c) so later statements can use the computed value.
            Note n = map(c);
            // Stores the result of tags.getForNote(id) in n.tags for use by later logic.
            n.tags = tags.getForNote(id);
            // Returns n to the caller.
            return n;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested all.
    public List<Note> getAll() {
        // Initializes q with new NoteQuery() so later statements can use the computed value.
        NoteQuery q = new NoteQuery();
        // Stores the result of true in q.pinnedFirst for use by later logic.
        q.pinnedFirst = true;
        // Returns query(q) to the caller.
        return query(q);
    // Closes the current method, control-flow, or type block.
    }

    // Declares escapeLike() to implement this part of the component.
    private static String escapeLike(String s) {
        // Returns s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") to the caller.
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    // Closes the current method, control-flow, or type block.
    }

    /** Builds one parameterised query from the filter; no user text is concatenated into SQL. */
    // Builds and runs a database query using the supplied filters.
    public List<Note> query(NoteQuery q) {
        // Initializes args with new ArrayList<>() so later statements can use the computed value.
        List<String> args = new ArrayList<>();
        // Initializes sql with the text or query fragment shown here.
        StringBuilder sql = new StringBuilder("SELECT n.* FROM notes n WHERE 1=1");
        // Initializes kw with the text or query fragment shown here.
        String kw = q.keyword == null ? "" : q.keyword.trim();
        // Initializes like with the text or query fragment shown here.
        String like = "%" + escapeLike(kw) + "%";

        // If !kw.isEmpty() is true, { immediately.
        if (!kw.isEmpty()) {
            // Calls sql.append with " AND (n.title LIKE ? ESCAPE '\\' OR n.content LIKE ? ESCAPE '\\')" to perform the operation shown.
            sql.append(" AND (n.title LIKE ? ESCAPE '\\' OR n.content LIKE ? ESCAPE '\\')");
            // Calls args.add with like to perform the operation shown.
            args.add(like);
            // Calls args.add with like to perform the operation shown.
            args.add(like);
        // Closes the current method, control-flow, or type block.
        }
        // If q.noteType != null is true, { immediately.
        if (q.noteType != null) {
            // Calls sql.append with " AND n.note_type=?" to perform the operation shown.
            sql.append(" AND n.note_type=?");
            // Calls args.add with q.noteType to perform the operation shown.
            args.add(q.noteType);
        // Closes the current method, control-flow, or type block.
        }
        // If q.favoritesOnly is true, sql.append(" AND n.is_favorite=1") immediately.
        if (q.favoritesOnly) sql.append(" AND n.is_favorite=1");
        // If q.pinnedOnly is true, sql.append(" AND n.is_pinned=1") immediately.
        if (q.pinnedOnly) sql.append(" AND n.is_pinned=1");
        // If q.tagIds != null && !q.tagIds.isEmpty() is true, { immediately.
        if (q.tagIds != null && !q.tagIds.isEmpty()) {
            // Calls sql.append with " AND EXISTS (SELECT 1 FROM note_tags nt WHERE nt.note_id=n.id AND nt.tag_id IN (" to perform the operation shown.
            sql.append(" AND EXISTS (SELECT 1 FROM note_tags nt WHERE nt.note_id=n.id AND nt.tag_id IN (");
            // Repeats the following block according to the initialization, condition, and update in int i = 0; i < q.tagIds.size(); i++.
            for (int i = 0; i < q.tagIds.size(); i++) {
                // Calls sql.append with i == 0 ? "?" : ",?" to perform the operation shown.
                sql.append(i == 0 ? "?" : ",?");
                // Calls args.add with String.valueOf(q.tagIds.get(i to perform the operation shown.
                args.add(String.valueOf(q.tagIds.get(i)));
            // Closes the current method, control-flow, or type block.
            }
            // Calls sql.append with "))" to perform the operation shown.
            sql.append("))");
        // Closes the current method, control-flow, or type block.
        }
        // If q.dayStart > 0 || q.hour >= 0 is true, { immediately.
        if (q.dayStart > 0 || q.hour >= 0) {
            // Calls sql.append with " AND (" to perform the operation shown.
            sql.append(" AND (");
            // Calls sql.append with dateTimeClause("created_at", q, args to perform the operation shown.
            sql.append(dateTimeClause("created_at", q, args));
            // Calls sql.append with " OR " to perform the operation shown.
            sql.append(" OR ");
            // Calls sql.append with dateTimeClause("updated_at", q, args to perform the operation shown.
            sql.append(dateTimeClause("updated_at", q, args));
            // Calls sql.append with ")" to perform the operation shown.
            sql.append(")");
        // Closes the current method, control-flow, or type block.
        }
        // If q.todoStatus != null || q.dueDayStart > 0 is true, { immediately.
        if (q.todoStatus != null || q.dueDayStart > 0) {
            // Calls sql.append with " AND EXISTS (SELECT 1 FROM todos t WHERE t.note_id=n.id" to perform the operation shown.
            sql.append(" AND EXISTS (SELECT 1 FROM todos t WHERE t.note_id=n.id");
            // If q.todoStatus != null is true, { immediately.
            if (q.todoStatus != null) {
                // Calls sql.append with " AND t.status=?" to perform the operation shown.
                sql.append(" AND t.status=?");
                // Calls args.add with q.todoStatus to perform the operation shown.
                args.add(q.todoStatus);
            // Closes the current method, control-flow, or type block.
            }
            // If q.dueDayStart > 0 is true, { immediately.
            if (q.dueDayStart > 0) {
                // Calls sql.append with " AND t.due_at BETWEEN ? AND ?" to perform the operation shown.
                sql.append(" AND t.due_at BETWEEN ? AND ?");
                // Calls args.add with String.valueOf(q.dueDayStart to perform the operation shown.
                args.add(String.valueOf(q.dueDayStart));
                // Calls args.add with String.valueOf(q.dueDayEnd to perform the operation shown.
                args.add(String.valueOf(q.dueDayEnd));
            // Closes the current method, control-flow, or type block.
            }
            // Calls sql.append with ")" to perform the operation shown.
            sql.append(")");
        // Closes the current method, control-flow, or type block.
        }

        // Calls sql.append with " ORDER BY " to perform the operation shown.
        sql.append(" ORDER BY ");
        // If q.pinnedFirst is true, sql.append("n.is_pinned DESC, ") immediately.
        if (q.pinnedFirst) sql.append("n.is_pinned DESC, ");
        // If SORT_OLDEST.equals(q.sort) is true, { immediately.
        if (SORT_OLDEST.equals(q.sort)) {
            // Calls sql.append with "n.created_at ASC" to perform the operation shown.
            sql.append("n.created_at ASC");
        // Checks the alternate condition (SORT_TITLE.equals(q.sort) after the earlier branch did not run.
        } else if (SORT_TITLE.equals(q.sort)) {
            // Calls sql.append with "n.title COLLATE NOCASE ASC" to perform the operation shown.
            sql.append("n.title COLLATE NOCASE ASC");
        // Checks the alternate condition (SORT_RELEVANCE.equals(q.sort) after the earlier branch did not run.
        } else if (SORT_RELEVANCE.equals(q.sort) && !kw.isEmpty()) {
            // Calls sql.append with "CASE WHEN n.title LIKE ? ESCAPE '\\' THEN 0 ELSE 1 END, n.updated_at DESC" to perform the operation shown.
            sql.append("CASE WHEN n.title LIKE ? ESCAPE '\\' THEN 0 ELSE 1 END, n.updated_at DESC");
            // Calls args.add with like to perform the operation shown.
            args.add(like);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls sql.append with "n.updated_at DESC" to perform the operation shown.
            sql.append("n.updated_at DESC");
        // Closes the current method, control-flow, or type block.
        }

        // Initializes result with new ArrayList<>() so later statements can use the computed value.
        List<Note> result = new ArrayList<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            // If c.moveToNext() is true, result.add(map(c)) immediately.
            while (c.moveToNext()) result.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Initializes tagMap with tags.noteTagMap() so later statements can use the computed value.
        Map<Long, List<Tag>> tagMap = tags.noteTagMap();
        // Repeats the following block according to the initialization, condition, and update in Note n : result.
        for (Note n : result) {
            // Initializes t with tagMap.get(n.id) so later statements can use the computed value.
            List<Tag> t = tagMap.get(n.id);
            // Stores the result of t == null ? new ArrayList<Tag>() : t in n.tags for use by later logic.
            n.tags = t == null ? new ArrayList<Tag>() : t;
        // Closes the current method, control-flow, or type block.
        }

        // If !q.favoritesOnly && !q.pinnedOnly && (q.noteType == null || Constants.NOTE_TYPE_TODO.equals(q.noteType)) is true, { immediately.
        if (!q.favoritesOnly && !q.pinnedOnly && (q.noteType == null || Constants.NOTE_TYPE_TODO.equals(q.noteType))) {
            // Initializes todoArgs with new ArrayList<>() so later statements can use the computed value.
            List<String> todoArgs = new ArrayList<>();
            // Initializes todoSql with the text or query fragment shown here.
            StringBuilder todoSql = new StringBuilder("SELECT t.* FROM todos t WHERE t.note_id IS NULL");
            // If !kw.isEmpty() is true, { immediately.
            if (!kw.isEmpty()) {
                // Calls todoSql.append with " AND (t.title LIKE ? ESCAPE '\\' OR t.description LIKE ? ESCAPE '\\')" to perform the operation shown.
                todoSql.append(" AND (t.title LIKE ? ESCAPE '\\' OR t.description LIKE ? ESCAPE '\\')");
                // Calls todoArgs.add with like to perform the operation shown.
                todoArgs.add(like);
                // Calls todoArgs.add with like to perform the operation shown.
                todoArgs.add(like);
            // Closes the current method, control-flow, or type block.
            }
            // If q.tagIds != null && !q.tagIds.isEmpty() is true, { immediately.
            if (q.tagIds != null && !q.tagIds.isEmpty()) {
                // Calls todoSql.append with " AND EXISTS (SELECT 1 FROM todo_tags tt WHERE tt.todo_id=t.id AND tt.tag_id IN (" to perform the operation shown.
                todoSql.append(" AND EXISTS (SELECT 1 FROM todo_tags tt WHERE tt.todo_id=t.id AND tt.tag_id IN (");
                // Repeats the following block according to the initialization, condition, and update in int i = 0; i < q.tagIds.size(); i++.
                for (int i = 0; i < q.tagIds.size(); i++) {
                    // Calls todoSql.append with i == 0 ? "?" : ",?" to perform the operation shown.
                    todoSql.append(i == 0 ? "?" : ",?");
                    // Calls todoArgs.add with String.valueOf(q.tagIds.get(i to perform the operation shown.
                    todoArgs.add(String.valueOf(q.tagIds.get(i)));
                // Closes the current method, control-flow, or type block.
                }
                // Calls todoSql.append with "))" to perform the operation shown.
                todoSql.append("))");
            // Closes the current method, control-flow, or type block.
            }
            // If q.todoStatus != null is true, { immediately.
            if (q.todoStatus != null) {
                // Calls todoSql.append with " AND t.status=?" to perform the operation shown.
                todoSql.append(" AND t.status=?");
                // Calls todoArgs.add with q.todoStatus to perform the operation shown.
                todoArgs.add(q.todoStatus);
            // Closes the current method, control-flow, or type block.
            }
            // If q.dueDayStart > 0 is true, { immediately.
            if (q.dueDayStart > 0) {
                // Calls todoSql.append with " AND t.due_at BETWEEN ? AND ?" to perform the operation shown.
                todoSql.append(" AND t.due_at BETWEEN ? AND ?");
                // Calls todoArgs.add with String.valueOf(q.dueDayStart to perform the operation shown.
                todoArgs.add(String.valueOf(q.dueDayStart));
                // Calls todoArgs.add with String.valueOf(q.dueDayEnd to perform the operation shown.
                todoArgs.add(String.valueOf(q.dueDayEnd));
            // Closes the current method, control-flow, or type block.
            }
            // If q.dayStart > 0 is true, { immediately.
            if (q.dayStart > 0) {
                // Calls todoSql.append with " AND t.created_at BETWEEN ? AND ?" to perform the operation shown.
                todoSql.append(" AND t.created_at BETWEEN ? AND ?");
                // Calls todoArgs.add with String.valueOf(q.dayStart to perform the operation shown.
                todoArgs.add(String.valueOf(q.dayStart));
                // Calls todoArgs.add with String.valueOf(q.dueDayEnd > 0 ? q.dueDayEnd : q.dayEnd to perform the operation shown.
                todoArgs.add(String.valueOf(q.dueDayEnd > 0 ? q.dueDayEnd : q.dayEnd));
            // Closes the current method, control-flow, or type block.
            }

            // Initializes todoTagMap with tags.todoTagMap() so later statements can use the computed value.
            Map<Long, List<Tag>> todoTagMap = tags.todoTagMap();
            // Runs this operation with automatic cleanup for the resources declared in the try statement.
            try (Cursor c = helper.getReadableDatabase().rawQuery(todoSql.toString(), todoArgs.toArray(new String[0]))) {
                // If c.moveToNext() is true, { immediately.
                while (c.moveToNext()) {
                    // Initializes todoId with the text or query fragment shown here.
                    long todoId = c.getLong(c.getColumnIndexOrThrow("id"));
                    // Initializes n with new Note() so later statements can use the computed value.
                    Note n = new Note();
                    // Stores the result of -todoId in n.id for use by later logic.
                    n.id = -todoId;
                    // Stores the result of c.getString(c.getColumnIndexOrThrow("title")) in n.title for use by later logic.
                    n.title = c.getString(c.getColumnIndexOrThrow("title"));
                    // Stores the result of c.getString(c.getColumnIndexOrThrow("description")) in n.content for use by later logic.
                    n.content = c.getString(c.getColumnIndexOrThrow("description"));
                    // Stores the result of "[]" in n.formatting for use by later logic.
                    n.formatting = "[]";
                    // Stores the result of Constants.NOTE_TYPE_TODO in n.noteType for use by later logic.
                    n.noteType = Constants.NOTE_TYPE_TODO;
                    // Stores the result of c.getLong(c.getColumnIndexOrThrow("created_at")) in n.createdAt for use by later logic.
                    n.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
                    // Initializes doneIdx with the text or query fragment shown here.
                    int doneIdx = c.getColumnIndexOrThrow("completed_at");
                    // Initializes doneAt with c.isNull(doneIdx) ? 0 : c.getLong(doneIdx) so later statements can use the computed value.
                    long doneAt = c.isNull(doneIdx) ? 0 : c.getLong(doneIdx);
                    // Stores the result of doneAt > 0 ? doneAt : n.createdAt in n.updatedAt for use by later logic.
                    n.updatedAt = doneAt > 0 ? doneAt : n.createdAt;
                    // Stores the result of TextStats.wordCount(n.content) in n.wordCount for use by later logic.
                    n.wordCount = TextStats.wordCount(n.content);
                    // Stores the result of TextStats.charCount(n.content) in n.charCount for use by later logic.
                    n.charCount = TextStats.charCount(n.content);
                    // Stores the result of false in n.favorite for use by later logic.
                    n.favorite = false;
                    // Stores the result of false in n.pinned for use by later logic.
                    n.pinned = false;
                    // Initializes tList with todoTagMap.get(todoId) so later statements can use the computed value.
                    List<Tag> tList = todoTagMap.get(todoId);
                    // Stores the result of tList == null ? new ArrayList<Tag>() : tList in n.tags for use by later logic.
                    n.tags = tList == null ? new ArrayList<Tag>() : tList;
                    // Calls result.add with n to perform the operation shown.
                    result.add(n);
                // Closes the current method, control-flow, or type block.
                }
            // Closes the current method, control-flow, or type block.
            }

            // Starts the callback body that handles the event or asynchronous result.
            java.util.Collections.sort(result, (n1, n2) -> {
                // If q.pinnedFirst && n1.pinned != n2.pinned is true, { immediately.
                if (q.pinnedFirst && n1.pinned != n2.pinned) {
                    // Returns n1.pinned ? -1 : 1 to the caller.
                    return n1.pinned ? -1 : 1;
                // Closes the current method, control-flow, or type block.
                }
                // If SORT_OLDEST.equals(q.sort) is true, { immediately.
                if (SORT_OLDEST.equals(q.sort)) {
                    // Returns Long.compare(n1.createdAt, n2.createdAt) to the caller.
                    return Long.compare(n1.createdAt, n2.createdAt);
                // Checks the alternate condition (SORT_TITLE.equals(q.sort) after the earlier branch did not run.
                } else if (SORT_TITLE.equals(q.sort)) {
                    // Returns n1.title.compareToIgnoreCase(n2.title) to the caller.
                    return n1.title.compareToIgnoreCase(n2.title);
                // Checks the alternate condition (SORT_RELEVANCE.equals(q.sort) after the earlier branch did not run.
                } else if (SORT_RELEVANCE.equals(q.sort) && !kw.isEmpty()) {
                    // Initializes m1 with n1.title.toLowerCase(Locale.getDefault()).contains(kw.toLowerCase(Locale.getDefault())) so later statements can use the computed value.
                    boolean m1 = n1.title.toLowerCase(Locale.getDefault()).contains(kw.toLowerCase(Locale.getDefault()));
                    // Initializes m2 with n2.title.toLowerCase(Locale.getDefault()).contains(kw.toLowerCase(Locale.getDefault())) so later statements can use the computed value.
                    boolean m2 = n2.title.toLowerCase(Locale.getDefault()).contains(kw.toLowerCase(Locale.getDefault()));
                    // If m1 != m2 is true, return m1 ? -1 : 1 immediately.
                    if (m1 != m2) return m1 ? -1 : 1;
                    // Returns Long.compare(n2.updatedAt, n1.updatedAt) to the caller.
                    return Long.compare(n2.updatedAt, n1.updatedAt);
                // Runs the fallback branch when the preceding condition did not match.
                } else {
                    // Returns Long.compare(n2.updatedAt, n1.updatedAt) to the caller.
                    return Long.compare(n2.updatedAt, n1.updatedAt);
                // Closes the current method, control-flow, or type block.
                }
            // Executes }) as a statement.
            });
        // Closes the current method, control-flow, or type block.
        }

        // Returns result to the caller.
        return result;
    // Closes the current method, control-flow, or type block.
    }

    /** Date and/or hour condition on one timestamp column (column name is an internal constant). */
    // Declares dateTimeClause() to implement this part of the component.
    private String dateTimeClause(String column, NoteQuery q, List<String> args) {
        // Initializes sb with the text or query fragment shown here.
        StringBuilder sb = new StringBuilder("(1=1");
        // If q.dayStart > 0 is true, { immediately.
        if (q.dayStart > 0) {
            // Calls sb.append with " AND n.").append(column).append(" BETWEEN ? AND ?" to perform the operation shown.
            sb.append(" AND n.").append(column).append(" BETWEEN ? AND ?");
            // Calls args.add with String.valueOf(q.dayStart to perform the operation shown.
            args.add(String.valueOf(q.dayStart));
            // Calls args.add with String.valueOf(q.dayEnd to perform the operation shown.
            args.add(String.valueOf(q.dayEnd));
        // Closes the current method, control-flow, or type block.
        }
        // If q.hour >= 0 is true, { immediately.
        if (q.hour >= 0) {
            // Calls sb.append with " AND strftime('%H', n.").append(column).append("/1000, 'unixepoch', 'localtime') = ?" to perform the operation shown.
            sb.append(" AND strftime('%H', n.").append(column).append("/1000, 'unixepoch', 'localtime') = ?");
            // Calls args.add with String.format(Locale.US, "%02d", q.hour to perform the operation shown.
            args.add(String.format(Locale.US, "%02d", q.hour));
        // Closes the current method, control-flow, or type block.
        }
        // Returns sb.append(")").toString() to the caller.
        return sb.append(")").toString();
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ writes

    /**
     * Creates (id == 0) or updates a note and replaces its tags, in a single transaction.
     * Word/character counts and timestamps are computed here so they are always consistent.
     *
     * @return the note id
     * @throws NoteLimitException when creating a note while 50 notes already exist
     */
    // Validates and persists the values currently entered in the form.
    public long save(Note n, List<Long> tagIds) throws NoteLimitException {
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Initializes isNew with n.id == 0 so later statements can use the computed value.
        boolean isNew = n.id == 0;
        // Initializes now with System.currentTimeMillis() so later statements can use the computed value.
        long now = System.currentTimeMillis();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Stores the result of n.title.trim() in n.title for use by later logic.
            n.title = n.title.trim();
            // Stores the result of TextStats.wordCount(n.content) in n.wordCount for use by later logic.
            n.wordCount = TextStats.wordCount(n.content);
            // Stores the result of TextStats.charCount(n.content) in n.charCount for use by later logic.
            n.charCount = TextStats.charCount(n.content);
            // Stores the result of now in n.updatedAt for use by later logic.
            n.updatedAt = now;

            // Initializes v with new ContentValues() so later statements can use the computed value.
            ContentValues v = new ContentValues();
            // Calls v.put with "title", n.title to perform the operation shown.
            v.put("title", n.title);
            // Calls v.put with "content", n.content to perform the operation shown.
            v.put("content", n.content);
            // Calls v.put with "formatting", n.formatting to perform the operation shown.
            v.put("formatting", n.formatting);
            // Calls v.put with "updated_at", now to perform the operation shown.
            v.put("updated_at", now);
            // Calls v.put with "word_count", n.wordCount to perform the operation shown.
            v.put("word_count", n.wordCount);
            // Calls v.put with "character_count", n.charCount to perform the operation shown.
            v.put("character_count", n.charCount);
            // Calls v.put with "is_favorite", n.favorite ? 1 : 0 to perform the operation shown.
            v.put("is_favorite", n.favorite ? 1 : 0);
            // Calls v.put with "is_pinned", n.pinned ? 1 : 0 to perform the operation shown.
            v.put("is_pinned", n.pinned ? 1 : 0);
            // Calls v.put with "note_type", n.noteType to perform the operation shown.
            v.put("note_type", n.noteType);

            // If isNew is true, { immediately.
            if (isNew) {
                // If scalarIn(db, "SELECT COUNT(*) FROM notes") >= Constants.MAX_NOTES is true, { immediately.
                if (scalarIn(db, "SELECT COUNT(*) FROM notes") >= Constants.MAX_NOTES) {
                    // Raises new NoteLimitException() so the caller can handle this failure.
                    throw new NoteLimitException();
                // Closes the current method, control-flow, or type block.
                }
                // Stores the result of now in n.createdAt for use by later logic.
                n.createdAt = now;
                // Calls v.put with "created_at", now to perform the operation shown.
                v.put("created_at", now);
                // Stores the result of db.insertOrThrow("notes", null, v) in n.id for use by later logic.
                n.id = db.insertOrThrow("notes", null, v);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Initializes rows with the text or query fragment shown here.
                int rows = db.update("notes", v, "id=?", new String[]{String.valueOf(n.id)});
                // If rows == 0 is true, throw new IllegalStateException("Note " + n.id + " no longer exists") immediately.
                if (rows == 0) throw new IllegalStateException("Note " + n.id + " no longer exists");
            // Closes the current method, control-flow, or type block.
            }
            // Calls TagRepository.replaceNoteTags with db, n.id, tagIds to perform the operation shown.
            TagRepository.replaceNoteTags(db, n.id, tagIds);
            // Calls syncTodo with db, n to perform the operation shown.
            syncTodo(db, n);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // Calls recordEvent with isNew ? R.string.notif_note_created : R.string.notif_note_updated, n.title, n.id to perform the operation shown.
        recordEvent(isNew ? R.string.notif_note_created : R.string.notif_note_updated, n.title, n.id);
        // Returns n.id to the caller.
        return n.id;
    // Closes the current method, control-flow, or type block.
    }

    // Declares scalarIn() to implement this part of the component.
    private static int scalarIn(SQLiteDatabase db, String sql) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = db.rawQuery(sql, null)) {
            // Returns c.moveToFirst() ? c.getInt(0) : 0 to the caller.
            return c.moveToFirst() ? c.getInt(0) : 0;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** Keeps the linked to-do row in step with the note (create, update or remove). */
    // Declares syncTodo() to implement this part of the component.
    private void syncTodo(SQLiteDatabase db, Note n) {
        // Initializes todoId with -1 so later statements can use the computed value.
        long todoId = -1;
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = db.rawQuery("SELECT id FROM todos WHERE note_id=?", new String[]{String.valueOf(n.id)})) {
            // If c.moveToFirst() is true, todoId = c.getLong(0) immediately.
            if (c.moveToFirst()) todoId = c.getLong(0);
        // Closes the current method, control-flow, or type block.
        }
        // If n.isTodo() is true, { immediately.
        if (n.isTodo()) {
            // Initializes v with new ContentValues() so later statements can use the computed value.
            ContentValues v = new ContentValues();
            // Calls v.put with "title", n.title to perform the operation shown.
            v.put("title", n.title);
            // Calls v.put with "description", n.content.length() > Constants.MAX_DESCRIPTION_LENGTH to perform the operation shown.
            v.put("description", n.content.length() > Constants.MAX_DESCRIPTION_LENGTH
                    // Calls n.content.substring with 0, Constants.MAX_DESCRIPTION_LENGTH) : n.content to perform the operation shown.
                    ? n.content.substring(0, Constants.MAX_DESCRIPTION_LENGTH) : n.content);
            // If todoId < 0 is true, { immediately.
            if (todoId < 0) {
                // Calls v.put with "note_id", n.id to perform the operation shown.
                v.put("note_id", n.id);
                // Calls v.put with "status", Constants.STATUS_PENDING to perform the operation shown.
                v.put("status", Constants.STATUS_PENDING);
                // Calls v.put with "created_at", System.currentTimeMillis( to perform the operation shown.
                v.put("created_at", System.currentTimeMillis());
                // Runs this operation with automatic cleanup for the resources declared in the try statement.
                try (Cursor a = db.rawQuery("SELECT scheduled_at, is_enabled, repeat_type, repeat_interval "
                        // Appends this literal fragment to the string or SQL query being assembled.
                        + "FROM alarms WHERE note_id=?", new String[]{String.valueOf(n.id)})) {
                    // If a.moveToFirst() is true, { immediately.
                    if (a.moveToFirst()) {
                        // Calls v.put with "due_at", a.getLong(0 to perform the operation shown.
                        v.put("due_at", a.getLong(0));
                        // Calls v.put with "reminder_enabled", a.getInt(1 to perform the operation shown.
                        v.put("reminder_enabled", a.getInt(1));
                        // Calls v.put with "repeat_type", a.getString(2 to perform the operation shown.
                        v.put("repeat_type", a.getString(2));
                        // Calls v.put with "repeat_interval", a.getInt(3 to perform the operation shown.
                        v.put("repeat_interval", a.getInt(3));
                    // Closes the current method, control-flow, or type block.
                    }
                // Closes the current method, control-flow, or type block.
                }
                // Calls db.insertOrThrow with "todos", null, v to perform the operation shown.
                db.insertOrThrow("todos", null, v);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Calls db.update with "todos", v, "id=?", new String[]{String.valueOf(todoId)} to perform the operation shown.
                db.update("todos", v, "id=?", new String[]{String.valueOf(todoId)});
            // Closes the current method, control-flow, or type block.
            }
        // Checks the alternate condition (todoId >= 0) after the earlier branch did not run.
        } else if (todoId >= 0) {
            // Calls db.delete with "todos", "id=?", new String[]{String.valueOf(todoId)} to perform the operation shown.
            db.delete("todos", "id=?", new String[]{String.valueOf(todoId)});
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Updates favorite.
    public void setFavorite(long id, boolean favorite) {
        // Calls helper.getWritableDatabase with ).execSQL("UPDATE notes SET is_favorite=? WHERE id=?", to perform the operation shown.
        helper.getWritableDatabase().execSQL("UPDATE notes SET is_favorite=? WHERE id=?",
                // Creates the object described by new Object[]{favorite ? 1 : 0, id}).
                new Object[]{favorite ? 1 : 0, id});
    // Closes the current method, control-flow, or type block.
    }

    // Updates pinned.
    public void setPinned(long id, boolean pinned) {
        // Calls helper.getWritableDatabase with ).execSQL("UPDATE notes SET is_pinned=? WHERE id=?", to perform the operation shown.
        helper.getWritableDatabase().execSQL("UPDATE notes SET is_pinned=? WHERE id=?",
                // Creates the object described by new Object[]{pinned ? 1 : 0, id}).
                new Object[]{pinned ? 1 : 0, id});
    // Closes the current method, control-flow, or type block.
    }

    /** Marks a note as a to-do item (or back to a plain note) and syncs the linked task. */
    // Updates note type.
    public void setNoteType(long id, String type) {
        // Initializes n with get(id) so later statements can use the computed value.
        Note n = get(id);
        // If n == null is true, return immediately.
        if (n == null) return;
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Stores the result of type in n.noteType for use by later logic.
            n.noteType = type;
            // Calls db.execSQL with "UPDATE notes SET note_type=?, updated_at=? WHERE id=?", to perform the operation shown.
            db.execSQL("UPDATE notes SET note_type=?, updated_at=? WHERE id=?",
                    // Creates the object described by new Object[]{type, System.currentTimeMillis(), id}).
                    new Object[]{type, System.currentTimeMillis(), id});
            // Calls syncTodo with db, n to perform the operation shown.
            syncTodo(db, n);
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

    // Updates note tags.
    public void setNoteTags(long id, List<Long> tagIds) {
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls TagRepository.replaceNoteTags with db, id, tagIds to perform the operation shown.
            TagRepository.replaceNoteTags(db, id, tagIds);
            // Calls db.execSQL with "UPDATE notes SET updated_at=? WHERE id=?", new Object[]{System.currentTimeMillis(), id} to perform the operation shown.
            db.execSQL("UPDATE notes SET updated_at=? WHERE id=?", new Object[]{System.currentTimeMillis(), id});
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

    /** Deletes a note, its linked task, tag links and alarm (cancelling the system alarm). */
    // Declares delete() to implement this part of the component.
    public void delete(long id) {
        // Initializes n with get(id) so later statements can use the computed value.
        Note n = get(id);
        // If n == null is true, return immediately.
        if (n == null) return;
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Initializes alarmId with -1 so later statements can use the computed value.
        long alarmId = -1;
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Runs this operation with automatic cleanup for the resources declared in the try statement.
            try (Cursor c = db.rawQuery("SELECT id FROM alarms WHERE note_id=?", new String[]{String.valueOf(id)})) {
                // If c.moveToFirst() is true, alarmId = c.getLong(0) immediately.
                if (c.moveToFirst()) alarmId = c.getLong(0);
            // Closes the current method, control-flow, or type block.
            }
            // Calls db.delete with "notes", "id=?", new String[]{String.valueOf(id)} to perform the operation shown.
            db.delete("notes", "id=?", new String[]{String.valueOf(id)});
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // If alarmId > 0 is true, AlarmScheduler.cancel(context, alarmId) immediately.
        if (alarmId > 0) AlarmScheduler.cancel(context, alarmId);
        // Calls recordEvent with R.string.notif_note_deleted, n.title, 0 to perform the operation shown.
        recordEvent(R.string.notif_note_deleted, n.title, 0);
    // Closes the current method, control-flow, or type block.
    }

    // Declares recordEvent() to implement this part of the component.
    private void recordEvent(int titleRes, String noteTitle, long noteId) {
        // If !new SettingsRepository(helper).getBool(Constants.SETTING_NOTE_HISTORY, true) is true, return immediately.
        if (!new SettingsRepository(helper).getBool(Constants.SETTING_NOTE_HISTORY, true)) return;
        // Creates the object described by new NotificationRepository(helper).add(context.getString(titleRes), noteTitle,.
        new NotificationRepository(helper).add(context.getString(titleRes), noteTitle,
                // Declares noteId for use by the surrounding method or class.
                Constants.CATEGORY_NOTE, noteId, 0);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
