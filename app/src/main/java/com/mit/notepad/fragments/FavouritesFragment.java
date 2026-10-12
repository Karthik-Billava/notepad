package com.mit.notepad.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.mit.notepad.adapters.NoteAdapter;
import com.mit.notepad.databinding.FragmentFavouritesBinding;
import com.mit.notepad.models.Note;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.UiUtils;

import java.util.List;

/** Screen 10: favourite notes, with filters, search, sorting and pin support. */
public class FavouritesFragment extends Fragment implements NoteAdapter.Listener {
    private FragmentFavouritesBinding b;
    private NoteRepository repo;
    private NoteAdapter adapter;
    private String keyword = "";
    private String sort = NoteRepository.SORT_NEWEST;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        b = FragmentFavouritesBinding.inflate(inflater, container, false);
        repo = new NoteRepository(requireContext());

        b.header.headerTitle.setText(R.string.title_favourites);
        b.header.headerSubtitle.setText(R.string.subtitle_favourites);
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        b.header.headerSearch.setOnClickListener(v -> {
            boolean show = b.etSearch.getVisibility() != View.VISIBLE;
            b.etSearch.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                b.etSearch.requestFocus();
            } else {
                b.etSearch.setText("");
                UiUtils.hideKeyboard(b.etSearch);
            }
        });
        b.header.headerMore.setOnClickListener(v -> {
            PopupMenu pm = new PopupMenu(requireContext(), v);
            pm.getMenu().add(0, 1, 0, R.string.sort_newest);
            pm.getMenu().add(0, 2, 1, R.string.sort_oldest);
            pm.getMenu().add(0, 3, 2, R.string.sort_title);
            pm.setOnMenuItemClickListener(item -> {
                sort = item.getItemId() == 2 ? NoteRepository.SORT_OLDEST
                        : item.getItemId() == 3 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_NEWEST;
                load();
                return true;
            });
            pm.show();
        });

        adapter = new NoteAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recycler.setAdapter(adapter);
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> load());
        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                load();
            }
        });
        b.fabAdd.setOnClickListener(v -> NoteActions.createNote(requireActivity()));
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        if (b == null) return;
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        q.favoritesOnly = true;
        q.keyword = keyword;
        q.sort = sort;
        q.pinnedFirst = true;
        int checked = b.chipGroup.getCheckedChipId();
        if (checked == R.id.chip_notes) q.noteType = Constants.NOTE_TYPE_NOTE;
        else if (checked == R.id.chip_todo) q.noteType = Constants.NOTE_TYPE_TODO;
        else if (checked == R.id.chip_pinned) q.pinnedOnly = true;
        List<Note> notes = repo.query(q);

        // counts for chips (favourites only)
        NoteRepository.NoteQuery all = new NoteRepository.NoteQuery();
        all.favoritesOnly = true;
        int total = repo.query(all).size();
        all.noteType = Constants.NOTE_TYPE_NOTE;
        int plain = repo.query(all).size();
        all.noteType = Constants.NOTE_TYPE_TODO;
        int todos = repo.query(all).size();
        all.noteType = null;
        all.pinnedOnly = true;
        int pinned = repo.query(all).size();
        b.chipAll.setText(getString(R.string.chip_all_count, total));
        b.chipNotes.setText(getString(R.string.chip_notes_count, plain));
        b.chipTodo.setText(getString(R.string.chip_todo_count, todos));
        b.chipPinned.setText(getString(R.string.chip_pinned_count, pinned));

        adapter.setListStyle(new SettingsRepository(requireContext()).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        adapter.setHighlight(keyword);
        adapter.setItems(notes);
        boolean empty = notes.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.empty.emptyTitle.setText(R.string.empty_fav_title);
        b.empty.emptyMessage.setText(R.string.empty_fav_message);
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
