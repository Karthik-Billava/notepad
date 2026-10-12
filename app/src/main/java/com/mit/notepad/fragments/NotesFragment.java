package com.mit.notepad.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mit.notepad.R;
import com.mit.notepad.activities.MainActivity;
import com.mit.notepad.activities.NoteActions;
import com.mit.notepad.activities.SearchActivity;
import com.mit.notepad.adapters.NoteAdapter;
import com.mit.notepad.databinding.FragmentNotesBinding;
import com.mit.notepad.models.Note;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.utils.Constants;

import java.util.List;

/** Screen 2: Notes list with summary, quick search, filters and sorting. */
public class NotesFragment extends Fragment implements NoteAdapter.Listener {
    private FragmentNotesBinding b;
    private NoteRepository repo;
    private NoteAdapter adapter;
    private String sort = NoteRepository.SORT_NEWEST;
    private String keyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        b = FragmentNotesBinding.inflate(inflater, container, false);
        repo = new NoteRepository(requireContext());

        SpannableString title = new SpannableString(getString(R.string.app_name));
        title.setSpan(new ForegroundColorSpan(requireContext().getColor(R.color.np_primary)), 4, title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        b.header.headerTitle.setText(title);
        b.header.headerSubtitle.setText(R.string.tagline);
        b.header.headerMore.setVisibility(View.GONE);
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        b.header.headerSearch.setOnClickListener(v -> startActivity(new Intent(requireContext(), SearchActivity.class)));

        adapter = new NoteAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recycler.setAdapter(adapter);

        b.chipGroup.setOnCheckedStateChangeListener((group, ids) -> load());
        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                load();
            }
        });
        b.btnSort.setOnClickListener(v -> showSortMenu());
        b.fabAdd.setOnClickListener(v -> NoteActions.createNote(requireActivity()));
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void showSortMenu() {
        PopupMenu pm = new PopupMenu(requireContext(), b.btnSort);
        pm.getMenu().add(0, 1, 0, R.string.sort_newest);
        pm.getMenu().add(0, 2, 1, R.string.sort_oldest);
        pm.getMenu().add(0, 3, 2, R.string.sort_title);
        pm.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            sort = id == 2 ? NoteRepository.SORT_OLDEST : id == 3 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_NEWEST;
            load();
            return true;
        });
        pm.show();
    }

    private void load() {
        if (b == null) return;
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        q.keyword = keyword;
        q.sort = sort;
        q.pinnedFirst = true; // pinned notes always on top
        int checked = b.chipGroup.getCheckedChipId();
        if (checked == R.id.chip_notes) q.noteType = Constants.NOTE_TYPE_NOTE;
        else if (checked == R.id.chip_todo) q.noteType = Constants.NOTE_TYPE_TODO;
        else if (checked == R.id.chip_fav) q.favoritesOnly = true;

        List<Note> notes = repo.query(q);
        adapter.setListStyle(new SettingsRepository(requireContext()).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        adapter.setHighlight(keyword);
        adapter.setItems(notes);

        int total = repo.count() + repo.countStandalone();
        int todos = repo.countByType(Constants.NOTE_TYPE_TODO);
        int plainNotes = repo.countByType(Constants.NOTE_TYPE_NOTE);
        int favs = repo.countFavorites();
        b.tvCountNotes.setText(getString(R.string.count_notes, total));
        b.tvCountTodo.setText(getString(R.string.count_todo, todos));
        b.tvCountFav.setText(getString(R.string.count_favourites, favs));
        b.chipAll.setText(getString(R.string.chip_all_count, total));
        b.chipNotes.setText(getString(R.string.chip_notes_count, plainNotes));
        b.chipTodo.setText(getString(R.string.chip_todo_count, todos));
        b.chipFav.setText(getString(R.string.chip_fav_count, favs));

        boolean empty = notes.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) {
            boolean filtered = !keyword.trim().isEmpty() || checked != R.id.chip_all;
            b.empty.emptyTitle.setText(filtered ? R.string.empty_no_match_title : R.string.empty_notes_title);
            b.empty.emptyMessage.setText(filtered ? R.string.empty_no_match_message : R.string.empty_notes_message);
        }
    }

    @Override public void onOpen(Note note) { NoteActions.open(requireActivity(), note); }

    @Override public void onFavoriteToggle(Note note) { NoteActions.toggleFavorite(requireActivity(), note, this::load); }

    @Override public void onMenu(Note note, View anchor) { NoteActions.showMenu(requireActivity(), anchor, note, this::load); }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
