package com.mit.notepad.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.mit.notepad.R;
import com.mit.notepad.adapters.NoteAdapter;
import com.mit.notepad.databinding.ActivityTagNotesBinding;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Tag;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.repositories.TagRepository;
import com.mit.notepad.utils.Constants;

import java.util.List;

/** Lists the notes that carry one tag (opened from the Tags screen). */
public class TagNotesActivity extends BaseActivity implements NoteAdapter.Listener {
    private ActivityTagNotesBinding b;
    private NoteAdapter adapter;
    private NoteRepository notes;
    private long tagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTagNotesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        notes = new NoteRepository(this);
        tagId = getIntent().getLongExtra(Constants.EXTRA_TAG_ID, 0);
        setupBackHeader(b.header, R.string.title_tags, R.string.subtitle_tags);
        adapter = new NoteAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        b.recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        Tag tag = new TagRepository(this).get(tagId);
        if (tag == null) {
            finish();
            return;
        }
        b.header.headerTitle.setText(String.format("#%s", tag.name));
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        q.tagIds.add(tagId);
        q.pinnedFirst = true;
        List<Note> list = notes.query(q);
        b.header.headerSubtitle.setText(getResources().getQuantityString(R.plurals.notes_count, list.size(), list.size()));
        adapter.setListStyle(new SettingsRepository(this).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        adapter.setItems(list);
        boolean empty = list.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.empty.emptyTitle.setText(R.string.empty_tag_notes_title);
        b.empty.emptyMessage.setText(R.string.empty_tag_notes_message);
    }

    @Override public void onOpen(Note note) { NoteActions.open(this, note); }

    @Override public void onFavoriteToggle(Note note) { NoteActions.toggleFavorite(this, note, this::load); }

    @Override public void onMenu(Note note, View anchor) { NoteActions.showMenu(this, anchor, note, this::load); }
}
