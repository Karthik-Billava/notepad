// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;

// Imports androidx.recyclerview.widget.LinearLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.LinearLayoutManager;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.adapters.NoteAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.NoteAdapter;
// Imports com.mit.notepad.databinding.ActivityTagNotesBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivityTagNotesBinding;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.repositories.TagRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TagRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Lists the notes that carry one tag (opened from the Tags screen). */
// Declares class TagNotesActivity to group related state and behavior.
public class TagNotesActivity extends BaseActivity implements NoteAdapter.Listener {
    // Declares b for use by the surrounding method or class.
    private ActivityTagNotesBinding b;
    // Declares adapter for use by the surrounding method or class.
    private NoteAdapter adapter;
    // Declares notes for use by the surrounding method or class.
    private NoteRepository notes;
    // Declares tagId for use by the surrounding method or class.
    private long tagId;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivityTagNotesBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivityTagNotesBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new NoteRepository(this) in notes for use by later logic.
        notes = new NoteRepository(this);
        // Stores the result of getIntent().getLongExtra(Constants.EXTRA_TAG_ID, 0) in tagId for use by later logic.
        tagId = getIntent().getLongExtra(Constants.EXTRA_TAG_ID, 0);
        // Calls setupBackHeader with b.header, R.string.title_tags, R.string.subtitle_tags to perform the operation shown.
        setupBackHeader(b.header, R.string.title_tags, R.string.subtitle_tags);
        // Stores the result of new NoteAdapter(this) in adapter for use by later logic.
        adapter = new NoteAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(this to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    protected void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // Initializes tag with new TagRepository(this).get(tagId) so later statements can use the computed value.
        Tag tag = new TagRepository(this).get(tagId);
        // If tag == null is true, { immediately.
        if (tag == null) {
            // Calls finish with  to perform the operation shown.
            finish();
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Calls b.header.headerTitle.setText with String.format("#%s", tag.name to perform the operation shown.
        b.header.headerTitle.setText(String.format("#%s", tag.name));
        // Initializes q with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        // Calls q.tagIds.add with tagId to perform the operation shown.
        q.tagIds.add(tagId);
        // Stores the result of true in q.pinnedFirst for use by later logic.
        q.pinnedFirst = true;
        // Initializes list with notes.query(q) so later statements can use the computed value.
        List<Note> list = notes.query(q);
        // Calls b.header.headerSubtitle.setText with getResources().getQuantityString(R.plurals.notes_count, list.size(), list.size( to perform the operation shown.
        b.header.headerSubtitle.setText(getResources().getQuantityString(R.plurals.notes_count, list.size(), list.size()));
        // Calls adapter.setListStyle with new SettingsRepository(this).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT to perform the operation shown.
        adapter.setListStyle(new SettingsRepository(this).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        // Calls adapter.setItems with list to perform the operation shown.
        adapter.setItems(list);
        // Initializes empty with list.isEmpty() so later statements can use the computed value.
        boolean empty = list.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.empty.emptyTitle.setText with R.string.empty_tag_notes_title to perform the operation shown.
        b.empty.emptyTitle.setText(R.string.empty_tag_notes_title);
        // Calls b.empty.emptyMessage.setText with R.string.empty_tag_notes_message to perform the operation shown.
        b.empty.emptyMessage.setText(R.string.empty_tag_notes_message);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override public void onOpen metadata to the declaration that follows.
    @Override public void onOpen(Note note) { NoteActions.open(this, note); }

    // Applies @Override public void onFavoriteToggle metadata to the declaration that follows.
    @Override public void onFavoriteToggle(Note note) { NoteActions.toggleFavorite(this, note, this::load); }

    // Applies @Override public void onMenu metadata to the declaration that follows.
    @Override public void onMenu(Note note, View anchor) { NoteActions.showMenu(this, anchor, note, this::load); }
// Closes the current method, control-flow, or type block.
}
