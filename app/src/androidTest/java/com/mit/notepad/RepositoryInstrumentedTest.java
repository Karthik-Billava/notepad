package com.mit.notepad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.repositories.AlarmRepository;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.TagRepository;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Database, 50-note limit, search, task status, tag relationship and alarm scheduling tests. */
@RunWith(AndroidJUnit4.class)
public class RepositoryInstrumentedTest {
    private Context context;
    private NotepadDatabase db;
    private NoteRepository notes;
    private TodoRepository todos;
    private TagRepository tags;
    private AlarmRepository alarms;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        db = new NotepadDatabase(context, null); // in-memory database
        notes = new NoteRepository(context, db);
        todos = new TodoRepository(context, db);
        tags = new TagRepository(db);
        alarms = new AlarmRepository(context, db);
    }

    @After
    public void tearDown() {
        for (Alarm a : alarms.getAll()) AlarmScheduler.cancel(context, a.id);
        db.close();
    }

    private long addNote(String title, String content) throws Exception {
        Note n = new Note();
        n.title = title;
        n.content = content;
        return notes.save(n, Collections.<Long>emptyList());
    }

    @Test
    public void noteIsStoredWithCountsAndTimestamps() throws Exception {
        long id = addNote("Ideas", "one two three");
        Note n = notes.get(id);
        assertEquals(3, n.wordCount);
        assertEquals(13, n.charCount);
        assertTrue(n.createdAt > 0);
        assertTrue(n.updatedAt >= n.createdAt);
    }

    @Test
    public void fiftyNoteLimitIsEnforced() throws Exception {
        for (int i = 0; i < Constants.MAX_NOTES; i++) addNote("Note " + i, "x");
        assertEquals(Constants.MAX_NOTES, notes.count());
        try {
            addNote("One too many", "x");
            fail("Expected NoteLimitException");
        } catch (NoteRepository.NoteLimitException expected) {
            assertEquals(Constants.MAX_NOTES, notes.count());
        }
        // Editing an existing note is still allowed at the limit
        Note first = notes.getAll().get(0);
        first.title = "Renamed";
        notes.save(first, Collections.<Long>emptyList());
    }

    @Test
    public void searchFindsKeywordInTitleAndContentWithFilters() throws Exception {
        long a = addNote("Project plan", "timeline");
        long b = addNote("Shopping", "buy project supplies");
        addNote("Journal", "nothing here");
        notes.setFavorite(b, true);

        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        q.keyword = "project";
        assertEquals(2, notes.query(q).size());

        q.favoritesOnly = true;
        List<Note> favs = notes.query(q);
        assertEquals(1, favs.size());
        assertEquals(b, favs.get(0).id);

        NoteRepository.NoteQuery wildcard = new NoteRepository.NoteQuery();
        wildcard.keyword = "%"; // must be treated literally, not as SQL wildcard
        assertEquals(0, notes.query(wildcard).size());

        NoteRepository.NoteQuery byDate = new NoteRepository.NoteQuery();
        long now = System.currentTimeMillis();
        byDate.dayStart = now - 24L * 3600 * 1000;
        byDate.dayEnd = now + 24L * 3600 * 1000;
        assertEquals(3, notes.query(byDate).size());
        byDate.dayStart = 1;
        byDate.dayEnd = 1000; // 1970
        assertEquals(0, notes.query(byDate).size());
        assertNotNull(notes.get(a));
    }

    @Test
    public void pinnedNotesAreListedFirst() throws Exception {
        long a = addNote("A", "x");
        long b = addNote("B", "x");
        notes.setPinned(a, true);
        List<Note> list = notes.getAll();
        assertEquals(a, list.get(0).id);
        assertEquals(b, list.get(1).id);
    }

    @Test
    public void markingNoteAsTodoCreatesPendingTaskAndCompletionIsTracked() throws Exception {
        Note n = new Note();
        n.title = "Finish report";
        n.content = "details";
        n.noteType = Constants.NOTE_TYPE_TODO;
        long id = notes.save(n, Collections.<Long>emptyList());

        Todo t = todos.getByNote(id);
        assertNotNull(t);
        assertFalse(t.completed);
        assertEquals(1, todos.count(Constants.STATUS_PENDING));

        todos.setCompleted(t.id, true);
        assertEquals(0, todos.count(Constants.STATUS_PENDING));
        assertEquals(1, todos.count(Constants.STATUS_COMPLETED));

        todos.setCompleted(t.id, false);
        assertEquals(1, todos.count(Constants.STATUS_PENDING));

        notes.setNoteType(id, Constants.NOTE_TYPE_NOTE);
        assertNull(todos.getByNote(id));
    }

    @Test
    public void standaloneTasksDoNotCountTowardNoteLimit() {
        for (int i = 0; i < Constants.MAX_NOTES + 5; i++) {
            Todo t = new Todo();
            t.title = "Task " + i;
            todos.save(t, Collections.<Long>emptyList());
        }
        assertEquals(0, notes.count());
        assertEquals(Constants.MAX_NOTES + 5, todos.count(Constants.STATUS_PENDING));
    }

    @Test
    public void tagRelationshipsSurviveTagAndNoteDeletion() throws Exception {
        long tagId;
        try {
            tagId = tags.create("Exams", "#7C6CF0", "label");
        } catch (TagRepository.DuplicateTagException e) {
            throw new AssertionError(e);
        }
        Note n1 = new Note();
        n1.title = "N1";
        Note n2 = new Note();
        n2.title = "N2";
        List<Long> ids = new ArrayList<>();
        ids.add(tagId);
        long id1 = notes.save(n1, ids);
        long id2 = notes.save(n2, ids);
        assertEquals(2, tags.get(tagId).noteCount);
        assertEquals("Exams", notes.get(id1).tags.get(0).name);

        notes.delete(id1); // removes the link only
        assertEquals(1, tags.get(tagId).noteCount);

        tags.delete(tagId); // tag deleted, note kept
        assertNull(tags.get(tagId));
        assertNotNull(notes.get(id2));
        assertTrue(notes.get(id2).tags.isEmpty());
    }

    @Test(expected = TagRepository.DuplicateTagException.class)
    public void duplicateTagNamesAreRejected() throws Exception {
        tags.create("Study", "#000000", "label"); // "Study" is a default tag
    }

    @Test
    public void alarmsAreScheduledCancelledAndUniquePerOwner() throws Exception {
        long n1 = addNote("A", "x");
        long n2 = addNote("B", "x");
        long at = System.currentTimeMillis() + 3600_000L;

        long a1 = alarms.saveForNote(n1, at, true, Constants.REPEAT_NONE, 1);
        long a2 = alarms.saveForNote(n2, at, true, Constants.REPEAT_DAILY, 1);
        assertNotEquals(a1, a2);
        assertTrue(AlarmScheduler.isScheduled(context, a1));
        assertTrue(AlarmScheduler.isScheduled(context, a2));

        // re-saving the same note's alarm updates it instead of creating a duplicate
        assertEquals(a1, alarms.saveForNote(n1, at + 60_000L, true, Constants.REPEAT_NONE, 1));
        assertEquals(2, alarms.getAll().size());

        alarms.setEnabled(a1, false);
        assertFalse(AlarmScheduler.isScheduled(context, a1));
        assertTrue(AlarmScheduler.isScheduled(context, a2));

        alarms.delete(a2);
        assertFalse(AlarmScheduler.isScheduled(context, a2));
        assertNull(alarms.get(a2));
    }

    @Test
    public void completingTaskCancelsReminderAndReopeningRestoresIt() {
        Todo t = new Todo();
        t.title = "Call";
        t.dueAt = System.currentTimeMillis() + 7200_000L;
        t.reminderEnabled = true;
        long id = todos.save(t, Collections.<Long>emptyList());
        Alarm a = alarms.getForTodo(id);
        assertNotNull(a);
        assertTrue(AlarmScheduler.isScheduled(context, a.id));

        todos.setCompleted(id, true);
        assertFalse(AlarmScheduler.isScheduled(context, a.id));

        todos.setCompleted(id, false);
        assertTrue(AlarmScheduler.isScheduled(context, a.id));

        // editing does not create a second alarm
        Todo edited = todos.get(id);
        edited.title = "Call again";
        todos.save(edited, Collections.<Long>emptyList());
        assertEquals(1, alarms.getAll().size());

        todos.delete(id);
        assertFalse(AlarmScheduler.isScheduled(context, a.id));
    }

    @Test
    public void deletingNoteCancelsItsAlarm() throws Exception {
        long id = addNote("Meeting", "x");
        long alarmId = alarms.saveForNote(id, System.currentTimeMillis() + 3600_000L, true, Constants.REPEAT_NONE, 1);
        notes.delete(id);
        assertFalse(AlarmScheduler.isScheduled(context, alarmId));
        assertEquals(0, alarms.getAll().size());
    }

    @Test
    public void dataPersistsWhenDatabaseIsReopened() throws Exception {
        String name = "persist_test.db";
        context.deleteDatabase(name);
        NotepadDatabase first = new NotepadDatabase(context, name);
        NoteRepository repo1 = new NoteRepository(context, first);
        Note n = new Note();
        n.title = "Persistent";
        n.content = "still here";
        repo1.save(n, Collections.<Long>emptyList());
        first.close();

        NotepadDatabase second = new NotepadDatabase(context, name);
        NoteRepository repo2 = new NoteRepository(context, second);
        assertEquals(1, repo2.count());
        assertEquals("Persistent", repo2.getAll().get(0).title);
        second.close();
        context.deleteDatabase(name);
    }
}
