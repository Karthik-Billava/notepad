// Sets the package name to com.mit.notepad so this class belongs to the app module namespace.
package com.mit.notepad;

// Imports static org.junit.Assert.assertEquals so this file can use that type by its short name.
import static org.junit.Assert.assertEquals;
// Imports static org.junit.Assert.assertFalse so this file can use that type by its short name.
import static org.junit.Assert.assertFalse;
// Imports static org.junit.Assert.assertNotEquals so this file can use that type by its short name.
import static org.junit.Assert.assertNotEquals;
// Imports static org.junit.Assert.assertNotNull so this file can use that type by its short name.
import static org.junit.Assert.assertNotNull;
// Imports static org.junit.Assert.assertNull so this file can use that type by its short name.
import static org.junit.Assert.assertNull;
// Imports static org.junit.Assert.assertTrue so this file can use that type by its short name.
import static org.junit.Assert.assertTrue;
// Imports static org.junit.Assert.fail so this file can use that type by its short name.
import static org.junit.Assert.fail;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;

// Imports androidx.test.core.app.ApplicationProvider so this file can use that type by its short name.
import androidx.test.core.app.ApplicationProvider;
// Imports androidx.test.ext.junit.runners.AndroidJUnit4 so this file can use that type by its short name.
import androidx.test.ext.junit.runners.AndroidJUnit4;

// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.repositories.AlarmRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.AlarmRepository;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.TagRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TagRepository;
// Imports com.mit.notepad.repositories.TodoRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TodoRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

// Imports org.junit.After so this file can use that type by its short name.
import org.junit.After;
// Imports org.junit.Before so this file can use that type by its short name.
import org.junit.Before;
// Imports org.junit.Test so this file can use that type by its short name.
import org.junit.Test;
// Imports org.junit.runner.RunWith so this file can use that type by its short name.
import org.junit.runner.RunWith;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.Collections so this file can use that type by its short name.
import java.util.Collections;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Database, 50-note limit, search, task status, tag relationship and alarm scheduling tests. */
// Applies @RunWith metadata to the declaration that follows.
@RunWith(AndroidJUnit4.class)
// Declares class RepositoryInstrumentedTest to group related state and behavior.
public class RepositoryInstrumentedTest {
    // Declares context for use by the surrounding method or class.
    private Context context;
    // Declares db for use by the surrounding method or class.
    private NotepadDatabase db;
    // Declares notes for use by the surrounding method or class.
    private NoteRepository notes;
    // Declares todos for use by the surrounding method or class.
    private TodoRepository todos;
    // Declares tags for use by the surrounding method or class.
    private TagRepository tags;
    // Declares alarms for use by the surrounding method or class.
    private AlarmRepository alarms;

    // Applies @Before metadata to the declaration that follows.
    @Before
    // Tests the expected behavior of set up.
    public void setUp() {
        // Stores the result of ApplicationProvider.getApplicationContext() in context for use by later logic.
        context = ApplicationProvider.getApplicationContext();
        // Stores the result of new NotepadDatabase(context, null) in db for use by later logic.
        db = new NotepadDatabase(context, null); // in-memory database
        // Stores the result of new NoteRepository(context, db) in notes for use by later logic.
        notes = new NoteRepository(context, db);
        // Stores the result of new TodoRepository(context, db) in todos for use by later logic.
        todos = new TodoRepository(context, db);
        // Stores the result of new TagRepository(db) in tags for use by later logic.
        tags = new TagRepository(db);
        // Stores the result of new AlarmRepository(context, db) in alarms for use by later logic.
        alarms = new AlarmRepository(context, db);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @After metadata to the declaration that follows.
    @After
    // Tests the expected behavior of tear down.
    public void tearDown() {
        // Repeats the following block according to the initialization, condition, and update in Alarm a : alarms.getAll()) AlarmScheduler.cancel(context, a.id.
        for (Alarm a : alarms.getAll()) AlarmScheduler.cancel(context, a.id);
        // Calls db.close with  to perform the operation shown.
        db.close();
    // Closes the current method, control-flow, or type block.
    }

    // Tests the expected behavior of add note.
    private long addNote(String title, String content) throws Exception {
        // Initializes n with new Note() so later statements can use the computed value.
        Note n = new Note();
        // Stores the result of title in n.title for use by later logic.
        n.title = title;
        // Stores the result of content in n.content for use by later logic.
        n.content = content;
        // Returns notes.save(n, Collections.<Long>emptyList()) to the caller.
        return notes.save(n, Collections.<Long>emptyList());
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of note is stored with counts and timestamps.
    public void noteIsStoredWithCountsAndTimestamps() throws Exception {
        // Initializes id with the text or query fragment shown here.
        long id = addNote("Ideas", "one two three");
        // Initializes n with notes.get(id) so later statements can use the computed value.
        Note n = notes.get(id);
        // Calls assertEquals with 3, n.wordCount to perform the operation shown.
        assertEquals(3, n.wordCount);
        // Calls assertEquals with 13, n.charCount to perform the operation shown.
        assertEquals(13, n.charCount);
        // Calls assertTrue with n.createdAt > 0 to perform the operation shown.
        assertTrue(n.createdAt > 0);
        // Calls assertTrue with n.updatedAt >= n.createdAt to perform the operation shown.
        assertTrue(n.updatedAt >= n.createdAt);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of fifty note limit is enforced.
    public void fiftyNoteLimitIsEnforced() throws Exception {
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < Constants.MAX_NOTES; i++) addNote("Note " + i, "x".
        for (int i = 0; i < Constants.MAX_NOTES; i++) addNote("Note " + i, "x");
        // Calls assertEquals with Constants.MAX_NOTES, notes.count( to perform the operation shown.
        assertEquals(Constants.MAX_NOTES, notes.count());
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls addNote with "One too many", "x" to perform the operation shown.
            addNote("One too many", "x");
            // Calls fail with "Expected NoteLimitException" to perform the operation shown.
            fail("Expected NoteLimitException");
        // Calls catch with NoteRepository.NoteLimitException expected) { to perform the operation shown.
        } catch (NoteRepository.NoteLimitException expected) {
            // Calls assertEquals with Constants.MAX_NOTES, notes.count( to perform the operation shown.
            assertEquals(Constants.MAX_NOTES, notes.count());
        // Closes the current method, control-flow, or type block.
        }
        // Editing an existing note is still allowed at the limit
        // Initializes first with notes.getAll().get(0) so later statements can use the computed value.
        Note first = notes.getAll().get(0);
        // Stores the result of "Renamed" in first.title for use by later logic.
        first.title = "Renamed";
        // Calls notes.save with first, Collections.<Long>emptyList( to perform the operation shown.
        notes.save(first, Collections.<Long>emptyList());
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of search finds keyword in title and content with filters.
    public void searchFindsKeywordInTitleAndContentWithFilters() throws Exception {
        // Initializes a with the text or query fragment shown here.
        long a = addNote("Project plan", "timeline");
        // Initializes b with the text or query fragment shown here.
        long b = addNote("Shopping", "buy project supplies");
        // Calls addNote with "Journal", "nothing here" to perform the operation shown.
        addNote("Journal", "nothing here");
        // Calls notes.setFavorite with b, true to perform the operation shown.
        notes.setFavorite(b, true);

        // Initializes q with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        // Stores the result of "project" in q.keyword for use by later logic.
        q.keyword = "project";
        // Calls assertEquals with 2, notes.query(q).size( to perform the operation shown.
        assertEquals(2, notes.query(q).size());

        // Stores the result of true in q.favoritesOnly for use by later logic.
        q.favoritesOnly = true;
        // Initializes favs with notes.query(q) so later statements can use the computed value.
        List<Note> favs = notes.query(q);
        // Calls assertEquals with 1, favs.size( to perform the operation shown.
        assertEquals(1, favs.size());
        // Calls assertEquals with b, favs.get(0).id to perform the operation shown.
        assertEquals(b, favs.get(0).id);

        // Initializes wildcard with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery wildcard = new NoteRepository.NoteQuery();
        // Stores the result of "%" in wildcard.keyword for use by later logic.
        wildcard.keyword = "%"; // must be treated literally, not as SQL wildcard
        // Calls assertEquals with 0, notes.query(wildcard).size( to perform the operation shown.
        assertEquals(0, notes.query(wildcard).size());

        // Initializes byDate with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery byDate = new NoteRepository.NoteQuery();
        // Initializes now with System.currentTimeMillis() so later statements can use the computed value.
        long now = System.currentTimeMillis();
        // Stores the result of now - 24L * 3600 * 1000 in byDate.dayStart for use by later logic.
        byDate.dayStart = now - 24L * 3600 * 1000;
        // Stores the result of now + 24L * 3600 * 1000 in byDate.dayEnd for use by later logic.
        byDate.dayEnd = now + 24L * 3600 * 1000;
        // Calls assertEquals with 3, notes.query(byDate).size( to perform the operation shown.
        assertEquals(3, notes.query(byDate).size());
        // Stores the result of 1 in byDate.dayStart for use by later logic.
        byDate.dayStart = 1;
        // Stores the result of 1000 in byDate.dayEnd for use by later logic.
        byDate.dayEnd = 1000; // 1970
        // Calls assertEquals with 0, notes.query(byDate).size( to perform the operation shown.
        assertEquals(0, notes.query(byDate).size());
        // Calls assertNotNull with notes.get(a to perform the operation shown.
        assertNotNull(notes.get(a));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of pinned notes are listed first.
    public void pinnedNotesAreListedFirst() throws Exception {
        // Initializes a with the text or query fragment shown here.
        long a = addNote("A", "x");
        // Initializes b with the text or query fragment shown here.
        long b = addNote("B", "x");
        // Calls notes.setPinned with a, true to perform the operation shown.
        notes.setPinned(a, true);
        // Initializes list with notes.getAll() so later statements can use the computed value.
        List<Note> list = notes.getAll();
        // Calls assertEquals with a, list.get(0).id to perform the operation shown.
        assertEquals(a, list.get(0).id);
        // Calls assertEquals with b, list.get(1).id to perform the operation shown.
        assertEquals(b, list.get(1).id);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of marking note as todo creates pending task and completion is tracked.
    public void markingNoteAsTodoCreatesPendingTaskAndCompletionIsTracked() throws Exception {
        // Initializes n with new Note() so later statements can use the computed value.
        Note n = new Note();
        // Stores the result of "Finish report" in n.title for use by later logic.
        n.title = "Finish report";
        // Stores the result of "details" in n.content for use by later logic.
        n.content = "details";
        // Stores the result of Constants.NOTE_TYPE_TODO in n.noteType for use by later logic.
        n.noteType = Constants.NOTE_TYPE_TODO;
        // Initializes id with notes.save(n, Collections.<Long>emptyList()) so later statements can use the computed value.
        long id = notes.save(n, Collections.<Long>emptyList());

        // Initializes t with todos.getByNote(id) so later statements can use the computed value.
        Todo t = todos.getByNote(id);
        // Calls assertNotNull with t to perform the operation shown.
        assertNotNull(t);
        // Calls assertFalse with t.completed to perform the operation shown.
        assertFalse(t.completed);
        // Calls assertEquals with 1, todos.count(Constants.STATUS_PENDING to perform the operation shown.
        assertEquals(1, todos.count(Constants.STATUS_PENDING));

        // Calls todos.setCompleted with t.id, true to perform the operation shown.
        todos.setCompleted(t.id, true);
        // Calls assertEquals with 0, todos.count(Constants.STATUS_PENDING to perform the operation shown.
        assertEquals(0, todos.count(Constants.STATUS_PENDING));
        // Calls assertEquals with 1, todos.count(Constants.STATUS_COMPLETED to perform the operation shown.
        assertEquals(1, todos.count(Constants.STATUS_COMPLETED));

        // Calls todos.setCompleted with t.id, false to perform the operation shown.
        todos.setCompleted(t.id, false);
        // Calls assertEquals with 1, todos.count(Constants.STATUS_PENDING to perform the operation shown.
        assertEquals(1, todos.count(Constants.STATUS_PENDING));

        // Calls notes.setNoteType with id, Constants.NOTE_TYPE_NOTE to perform the operation shown.
        notes.setNoteType(id, Constants.NOTE_TYPE_NOTE);
        // Calls assertNull with todos.getByNote(id to perform the operation shown.
        assertNull(todos.getByNote(id));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of standalone tasks do not count toward note limit.
    public void standaloneTasksDoNotCountTowardNoteLimit() {
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < Constants.MAX_NOTES + 5; i++.
        for (int i = 0; i < Constants.MAX_NOTES + 5; i++) {
            // Initializes t with new Todo() so later statements can use the computed value.
            Todo t = new Todo();
            // Stores the result of "Task " + i in t.title for use by later logic.
            t.title = "Task " + i;
            // Calls todos.save with t, Collections.<Long>emptyList( to perform the operation shown.
            todos.save(t, Collections.<Long>emptyList());
        // Closes the current method, control-flow, or type block.
        }
        // Calls assertEquals with 0, notes.count( to perform the operation shown.
        assertEquals(0, notes.count());
        // Calls assertEquals with Constants.MAX_NOTES + 5, todos.count(Constants.STATUS_PENDING to perform the operation shown.
        assertEquals(Constants.MAX_NOTES + 5, todos.count(Constants.STATUS_PENDING));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of tag relationships survive tag and note deletion.
    public void tagRelationshipsSurviveTagAndNoteDeletion() throws Exception {
        // Declares tagId for use by the surrounding method or class.
        long tagId;
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Stores the result of tags.create("Exams", "#7C6CF0", "label") in tagId for use by later logic.
            tagId = tags.create("Exams", "#7C6CF0", "label");
        // Calls catch with TagRepository.DuplicateTagException e) { to perform the operation shown.
        } catch (TagRepository.DuplicateTagException e) {
            // Raises new AssertionError(e) so the caller can handle this failure.
            throw new AssertionError(e);
        // Closes the current method, control-flow, or type block.
        }
        // Initializes n1 with new Note() so later statements can use the computed value.
        Note n1 = new Note();
        // Stores the result of "N1" in n1.title for use by later logic.
        n1.title = "N1";
        // Initializes n2 with new Note() so later statements can use the computed value.
        Note n2 = new Note();
        // Stores the result of "N2" in n2.title for use by later logic.
        n2.title = "N2";
        // Initializes ids with new ArrayList<>() so later statements can use the computed value.
        List<Long> ids = new ArrayList<>();
        // Calls ids.add with tagId to perform the operation shown.
        ids.add(tagId);
        // Initializes id1 with notes.save(n1, ids) so later statements can use the computed value.
        long id1 = notes.save(n1, ids);
        // Initializes id2 with notes.save(n2, ids) so later statements can use the computed value.
        long id2 = notes.save(n2, ids);
        // Calls assertEquals with 2, tags.get(tagId).noteCount to perform the operation shown.
        assertEquals(2, tags.get(tagId).noteCount);
        // Calls assertEquals with "Exams", notes.get(id1).tags.get(0).name to perform the operation shown.
        assertEquals("Exams", notes.get(id1).tags.get(0).name);

        // Calls notes.delete with id1 to perform the operation shown.
        notes.delete(id1); // removes the link only
        // Calls assertEquals with 1, tags.get(tagId).noteCount to perform the operation shown.
        assertEquals(1, tags.get(tagId).noteCount);

        // Calls tags.delete with tagId to perform the operation shown.
        tags.delete(tagId); // tag deleted, note kept
        // Calls assertNull with tags.get(tagId to perform the operation shown.
        assertNull(tags.get(tagId));
        // Calls assertNotNull with notes.get(id2 to perform the operation shown.
        assertNotNull(notes.get(id2));
        // Calls assertTrue with notes.get(id2).tags.isEmpty( to perform the operation shown.
        assertTrue(notes.get(id2).tags.isEmpty());
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test(expected = TagRepository.DuplicateTagException.class)
    // Tests the expected behavior of duplicate tag names are rejected.
    public void duplicateTagNamesAreRejected() throws Exception {
        // Calls tags.create with "Study", "#000000", "label" to perform the operation shown.
        tags.create("Study", "#000000", "label"); // "Study" is a default tag
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of alarms are scheduled cancelled and unique per owner.
    public void alarmsAreScheduledCancelledAndUniquePerOwner() throws Exception {
        // Initializes n1 with the text or query fragment shown here.
        long n1 = addNote("A", "x");
        // Initializes n2 with the text or query fragment shown here.
        long n2 = addNote("B", "x");
        // Initializes at with System.currentTimeMillis() + 3600_000L so later statements can use the computed value.
        long at = System.currentTimeMillis() + 3600_000L;

        // Initializes a1 with alarms.saveForNote(n1, at, true, Constants.REPEAT_NONE, 1) so later statements can use the computed value.
        long a1 = alarms.saveForNote(n1, at, true, Constants.REPEAT_NONE, 1);
        // Initializes a2 with alarms.saveForNote(n2, at, true, Constants.REPEAT_DAILY, 1) so later statements can use the computed value.
        long a2 = alarms.saveForNote(n2, at, true, Constants.REPEAT_DAILY, 1);
        // Calls assertNotEquals with a1, a2 to perform the operation shown.
        assertNotEquals(a1, a2);
        // Calls assertTrue with AlarmScheduler.isScheduled(context, a1 to perform the operation shown.
        assertTrue(AlarmScheduler.isScheduled(context, a1));
        // Calls assertTrue with AlarmScheduler.isScheduled(context, a2 to perform the operation shown.
        assertTrue(AlarmScheduler.isScheduled(context, a2));

        // re-saving the same note's alarm updates it instead of creating a duplicate
        // Calls assertEquals with a1, alarms.saveForNote(n1, at + 60_000L, true, Constants.REPEAT_NONE, 1 to perform the operation shown.
        assertEquals(a1, alarms.saveForNote(n1, at + 60_000L, true, Constants.REPEAT_NONE, 1));
        // Calls assertEquals with 2, alarms.getAll().size( to perform the operation shown.
        assertEquals(2, alarms.getAll().size());

        // Calls alarms.setEnabled with a1, false to perform the operation shown.
        alarms.setEnabled(a1, false);
        // Calls assertFalse with AlarmScheduler.isScheduled(context, a1 to perform the operation shown.
        assertFalse(AlarmScheduler.isScheduled(context, a1));
        // Calls assertTrue with AlarmScheduler.isScheduled(context, a2 to perform the operation shown.
        assertTrue(AlarmScheduler.isScheduled(context, a2));

        // Calls alarms.delete with a2 to perform the operation shown.
        alarms.delete(a2);
        // Calls assertFalse with AlarmScheduler.isScheduled(context, a2 to perform the operation shown.
        assertFalse(AlarmScheduler.isScheduled(context, a2));
        // Calls assertNull with alarms.get(a2 to perform the operation shown.
        assertNull(alarms.get(a2));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of completing task cancels reminder and reopening restores it.
    public void completingTaskCancelsReminderAndReopeningRestoresIt() {
        // Initializes t with new Todo() so later statements can use the computed value.
        Todo t = new Todo();
        // Stores the result of "Call" in t.title for use by later logic.
        t.title = "Call";
        // Stores the result of System.currentTimeMillis() + 7200_000L in t.dueAt for use by later logic.
        t.dueAt = System.currentTimeMillis() + 7200_000L;
        // Stores the result of true in t.reminderEnabled for use by later logic.
        t.reminderEnabled = true;
        // Initializes id with todos.save(t, Collections.<Long>emptyList()) so later statements can use the computed value.
        long id = todos.save(t, Collections.<Long>emptyList());
        // Initializes a with alarms.getForTodo(id) so later statements can use the computed value.
        Alarm a = alarms.getForTodo(id);
        // Calls assertNotNull with a to perform the operation shown.
        assertNotNull(a);
        // Calls assertTrue with AlarmScheduler.isScheduled(context, a.id to perform the operation shown.
        assertTrue(AlarmScheduler.isScheduled(context, a.id));

        // Calls todos.setCompleted with id, true to perform the operation shown.
        todos.setCompleted(id, true);
        // Calls assertFalse with AlarmScheduler.isScheduled(context, a.id to perform the operation shown.
        assertFalse(AlarmScheduler.isScheduled(context, a.id));

        // Calls todos.setCompleted with id, false to perform the operation shown.
        todos.setCompleted(id, false);
        // Calls assertTrue with AlarmScheduler.isScheduled(context, a.id to perform the operation shown.
        assertTrue(AlarmScheduler.isScheduled(context, a.id));

        // editing does not create a second alarm
        // Initializes edited with todos.get(id) so later statements can use the computed value.
        Todo edited = todos.get(id);
        // Stores the result of "Call again" in edited.title for use by later logic.
        edited.title = "Call again";
        // Calls todos.save with edited, Collections.<Long>emptyList( to perform the operation shown.
        todos.save(edited, Collections.<Long>emptyList());
        // Calls assertEquals with 1, alarms.getAll().size( to perform the operation shown.
        assertEquals(1, alarms.getAll().size());

        // Calls todos.delete with id to perform the operation shown.
        todos.delete(id);
        // Calls assertFalse with AlarmScheduler.isScheduled(context, a.id to perform the operation shown.
        assertFalse(AlarmScheduler.isScheduled(context, a.id));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of deleting note cancels its alarm.
    public void deletingNoteCancelsItsAlarm() throws Exception {
        // Initializes id with the text or query fragment shown here.
        long id = addNote("Meeting", "x");
        // Initializes alarmId with alarms.saveForNote(id, System.currentTimeMillis() + 3600_000L, true, Constants.REPEAT_NONE, 1) so later statements can use the computed value.
        long alarmId = alarms.saveForNote(id, System.currentTimeMillis() + 3600_000L, true, Constants.REPEAT_NONE, 1);
        // Calls notes.delete with id to perform the operation shown.
        notes.delete(id);
        // Calls assertFalse with AlarmScheduler.isScheduled(context, alarmId to perform the operation shown.
        assertFalse(AlarmScheduler.isScheduled(context, alarmId));
        // Calls assertEquals with 0, alarms.getAll().size( to perform the operation shown.
        assertEquals(0, alarms.getAll().size());
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of data persists when database is reopened.
    public void dataPersistsWhenDatabaseIsReopened() throws Exception {
        // Initializes name with the text or query fragment shown here.
        String name = "persist_test.db";
        // Calls context.deleteDatabase with name to perform the operation shown.
        context.deleteDatabase(name);
        // Initializes first with new NotepadDatabase(context, name) so later statements can use the computed value.
        NotepadDatabase first = new NotepadDatabase(context, name);
        // Initializes repo1 with new NoteRepository(context, first) so later statements can use the computed value.
        NoteRepository repo1 = new NoteRepository(context, first);
        // Initializes n with new Note() so later statements can use the computed value.
        Note n = new Note();
        // Stores the result of "Persistent" in n.title for use by later logic.
        n.title = "Persistent";
        // Stores the result of "still here" in n.content for use by later logic.
        n.content = "still here";
        // Calls repo1.save with n, Collections.<Long>emptyList( to perform the operation shown.
        repo1.save(n, Collections.<Long>emptyList());
        // Calls first.close with  to perform the operation shown.
        first.close();

        // Initializes second with new NotepadDatabase(context, name) so later statements can use the computed value.
        NotepadDatabase second = new NotepadDatabase(context, name);
        // Initializes repo2 with new NoteRepository(context, second) so later statements can use the computed value.
        NoteRepository repo2 = new NoteRepository(context, second);
        // Calls assertEquals with 1, repo2.count( to perform the operation shown.
        assertEquals(1, repo2.count());
        // Calls assertEquals with "Persistent", repo2.getAll().get(0).title to perform the operation shown.
        assertEquals("Persistent", repo2.getAll().get(0).title);
        // Calls second.close with  to perform the operation shown.
        second.close();
        // Calls context.deleteDatabase with name to perform the operation shown.
        context.deleteDatabase(name);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Test metadata to the declaration that follows.
    @Test
    // Tests the expected behavior of standalone tasks listed in tag and notes count.
    public void standaloneTasksListedInTagAndNotesCount() throws Exception {
        // Initializes tagId with the text or query fragment shown here.
        long tagId = tags.create("WorkTag", "#F5B731", "work");

        // 1. Create a standalone todo assigned to tag
        // Initializes standalone with new Todo() so later statements can use the computed value.
        Todo standalone = new Todo();
        // Stores the result of "Buy supplies" in standalone.title for use by later logic.
        standalone.title = "Buy supplies";
        // Stores the result of "Office supplies" in standalone.description for use by later logic.
        standalone.description = "Office supplies";
        // Calls todos.save with standalone, Collections.singletonList(tagId to perform the operation shown.
        todos.save(standalone, Collections.singletonList(tagId));

        // 2. Create a note of type TODO assigned to tag
        // Initializes noteTodo with new Note() so later statements can use the computed value.
        Note noteTodo = new Note();
        // Stores the result of "Project milestone" in noteTodo.title for use by later logic.
        noteTodo.title = "Project milestone";
        // Stores the result of Constants.NOTE_TYPE_TODO in noteTodo.noteType for use by later logic.
        noteTodo.noteType = Constants.NOTE_TYPE_TODO;
        // Calls notes.save with noteTodo, Collections.singletonList(tagId to perform the operation shown.
        notes.save(noteTodo, Collections.singletonList(tagId));

        // Verify tag noteCount includes both standalone todo and note-based todo
        // Calls assertEquals with 2, tags.get(tagId).noteCount to perform the operation shown.
        assertEquals(2, tags.get(tagId).noteCount);

        // Verify total todo count in NoteRepository includes both
        // Calls assertEquals with 2, notes.countByType(Constants.NOTE_TYPE_TODO to perform the operation shown.
        assertEquals(2, notes.countByType(Constants.NOTE_TYPE_TODO));

        // Verify querying notes by tagId returns both items
        // Initializes tagQuery with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery tagQuery = new NoteRepository.NoteQuery();
        // Calls tagQuery.tagIds.add with tagId to perform the operation shown.
        tagQuery.tagIds.add(tagId);
        // Initializes tagResults with notes.query(tagQuery) so later statements can use the computed value.
        List<Note> tagResults = notes.query(tagQuery);
        // Calls assertEquals with 2, tagResults.size( to perform the operation shown.
        assertEquals(2, tagResults.size());

        // Verify querying notes by noteType TODO returns both items
        // Initializes todoQuery with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery todoQuery = new NoteRepository.NoteQuery();
        // Stores the result of Constants.NOTE_TYPE_TODO in todoQuery.noteType for use by later logic.
        todoQuery.noteType = Constants.NOTE_TYPE_TODO;
        // Initializes todoResults with notes.query(todoQuery) so later statements can use the computed value.
        List<Note> todoResults = notes.query(todoQuery);
        // Calls assertEquals with 2, todoResults.size( to perform the operation shown.
        assertEquals(2, todoResults.size());
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
