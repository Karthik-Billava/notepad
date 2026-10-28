// Sets the package name to com.mit.notepad.fragments so this class belongs to the app module namespace.
package com.mit.notepad.fragments;

// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.text.Editable so this file can use that type by its short name.
import android.text.Editable;
// Imports android.text.TextWatcher so this file can use that type by its short name.
import android.text.TextWatcher;
// Imports android.view.LayoutInflater so this file can use that type by its short name.
import android.view.LayoutInflater;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.view.ViewGroup so this file can use that type by its short name.
import android.view.ViewGroup;
// Imports android.widget.PopupMenu so this file can use that type by its short name.
import android.widget.PopupMenu;

// Imports androidx.annotation.NonNull so this file can use that type by its short name.
import androidx.annotation.NonNull;
// Imports androidx.annotation.Nullable so this file can use that type by its short name.
import androidx.annotation.Nullable;
// Imports androidx.fragment.app.Fragment so this file can use that type by its short name.
import androidx.fragment.app.Fragment;
// Imports androidx.recyclerview.widget.LinearLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.LinearLayoutManager;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.activities.MainActivity so this file can use that type by its short name.
import com.mit.notepad.activities.MainActivity;
// Imports com.mit.notepad.activities.NoteActions so this file can use that type by its short name.
import com.mit.notepad.activities.NoteActions;
// Imports com.mit.notepad.adapters.NoteAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.NoteAdapter;
// Imports com.mit.notepad.databinding.FragmentFavouritesBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.FragmentFavouritesBinding;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Screen 10: favourite notes, with filters, search, sorting and pin support. */
// Declares class FavouritesFragment to group related state and behavior.
public class FavouritesFragment extends Fragment implements NoteAdapter.Listener {
    // Declares b for use by the surrounding method or class.
    private FragmentFavouritesBinding b;
    // Declares repo for use by the surrounding method or class.
    private NoteRepository repo;
    // Declares adapter for use by the surrounding method or class.
    private NoteAdapter adapter;
    // Initializes keyword with the text or query fragment shown here.
    private String keyword = "";
    // Initializes sort with NoteRepository.SORT_NEWEST so later statements can use the computed value.
    private String sort = NoteRepository.SORT_NEWEST;

    // Applies @Nullable metadata to the declaration that follows.
    @Nullable
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Builds and returns the fragment view hierarchy.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        // Stores the result of FragmentFavouritesBinding.inflate(inflater, container, false) in b for use by later logic.
        b = FragmentFavouritesBinding.inflate(inflater, container, false);
        // Stores the result of new NoteRepository(requireContext()) in repo for use by later logic.
        repo = new NoteRepository(requireContext());

        // Calls b.header.headerTitle.setText with R.string.title_favourites to perform the operation shown.
        b.header.headerTitle.setText(R.string.title_favourites);
        // Calls b.header.headerSubtitle.setText with R.string.subtitle_favourites to perform the operation shown.
        b.header.headerSubtitle.setText(R.string.subtitle_favourites);
        // Calls b.header.headerNav.setOnClickListener with v -> ((MainActivity) requireActivity()).openDrawer( to perform the operation shown.
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        // Starts the callback body that handles the event or asynchronous result.
        b.header.headerSearch.setOnClickListener(v -> {
            // Initializes show with b.etSearch.getVisibility() != View.VISIBLE so later statements can use the computed value.
            boolean show = b.etSearch.getVisibility() != View.VISIBLE;
            // Calls b.etSearch.setVisibility with show ? View.VISIBLE : View.GONE to perform the operation shown.
            b.etSearch.setVisibility(show ? View.VISIBLE : View.GONE);
            // If show is true, { immediately.
            if (show) {
                // Calls b.etSearch.requestFocus with  to perform the operation shown.
                b.etSearch.requestFocus();
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Calls b.etSearch.setText with "" to perform the operation shown.
                b.etSearch.setText("");
                // Calls UiUtils.hideKeyboard with b.etSearch to perform the operation shown.
                UiUtils.hideKeyboard(b.etSearch);
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Starts the callback body that handles the event or asynchronous result.
        b.header.headerMore.setOnClickListener(v -> {
            // Initializes pm with new PopupMenu(requireContext(), v) so later statements can use the computed value.
            PopupMenu pm = new PopupMenu(requireContext(), v);
            // Calls pm.getMenu with ).add(0, 1, 0, R.string.sort_newest to perform the operation shown.
            pm.getMenu().add(0, 1, 0, R.string.sort_newest);
            // Calls pm.getMenu with ).add(0, 2, 1, R.string.sort_oldest to perform the operation shown.
            pm.getMenu().add(0, 2, 1, R.string.sort_oldest);
            // Calls pm.getMenu with ).add(0, 3, 2, R.string.sort_title to perform the operation shown.
            pm.getMenu().add(0, 3, 2, R.string.sort_title);
            // Starts the callback body that handles the event or asynchronous result.
            pm.setOnMenuItemClickListener(item -> {
                // Stores the result of item.getItemId() == 2 ? NoteRepository.SORT_OLDEST in sort for use by later logic.
                sort = item.getItemId() == 2 ? NoteRepository.SORT_OLDEST
                        // Calls item.getItemId with ) == 3 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_NEWEST to perform the operation shown.
                        : item.getItemId() == 3 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_NEWEST;
                // Calls load with  to perform the operation shown.
                load();
                // Returns true to the caller.
                return true;
            // Executes }) as a statement.
            });
            // Calls pm.show with  to perform the operation shown.
            pm.show();
        // Executes }) as a statement.
        });

        // Stores the result of new NoteAdapter(this) in adapter for use by later logic.
        adapter = new NoteAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(requireContext( to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);
        // Calls b.chipGroup.setOnCheckedStateChangeListener with (g, ids) -> load( to perform the operation shown.
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> load());
        // Calls b.etSearch.addTextChangedListener with new TextWatcher() { to perform the operation shown.
        b.etSearch.addTextChangedListener(new TextWatcher() {
            // Applies @Override public void beforeTextChanged metadata to the declaration that follows.
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            // Applies @Override public void onTextChanged metadata to the declaration that follows.
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Runs the registered update after the text value changes.
            public void afterTextChanged(Editable s) {
                // Stores the result of s.toString() in keyword for use by later logic.
                keyword = s.toString();
                // Calls load with  to perform the operation shown.
                load();
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Calls b.fabAdd.setOnClickListener with v -> NoteActions.createNote(requireActivity( to perform the operation shown.
        b.fabAdd.setOnClickListener(v -> NoteActions.createNote(requireActivity()));
        // Returns b.getRoot() to the caller.
        return b.getRoot();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    public void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // If b == null is true, return immediately.
        if (b == null) return;
        // Initializes q with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        // Stores the result of true in q.favoritesOnly for use by later logic.
        q.favoritesOnly = true;
        // Stores the result of keyword in q.keyword for use by later logic.
        q.keyword = keyword;
        // Stores the result of sort in q.sort for use by later logic.
        q.sort = sort;
        // Stores the result of true in q.pinnedFirst for use by later logic.
        q.pinnedFirst = true;
        // Initializes checked with b.chipGroup.getCheckedChipId() so later statements can use the computed value.
        int checked = b.chipGroup.getCheckedChipId();
        // If checked == R.id.chip_notes is true, q.noteType = Constants.NOTE_TYPE_NOTE immediately.
        if (checked == R.id.chip_notes) q.noteType = Constants.NOTE_TYPE_NOTE;
        // Checks the alternate condition (checked == R.id.chip_todo) after the earlier branch did not run.
        else if (checked == R.id.chip_todo) q.noteType = Constants.NOTE_TYPE_TODO;
        // Checks the alternate condition (checked == R.id.chip_pinned) after the earlier branch did not run.
        else if (checked == R.id.chip_pinned) q.pinnedOnly = true;
        // Initializes notes with repo.query(q) so later statements can use the computed value.
        List<Note> notes = repo.query(q);

        // counts for chips (favourites only)
        // Initializes all with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery all = new NoteRepository.NoteQuery();
        // Stores the result of true in all.favoritesOnly for use by later logic.
        all.favoritesOnly = true;
        // Initializes total with repo.query(all).size() so later statements can use the computed value.
        int total = repo.query(all).size();
        // Stores the result of Constants.NOTE_TYPE_NOTE in all.noteType for use by later logic.
        all.noteType = Constants.NOTE_TYPE_NOTE;
        // Initializes plain with repo.query(all).size() so later statements can use the computed value.
        int plain = repo.query(all).size();
        // Stores the result of Constants.NOTE_TYPE_TODO in all.noteType for use by later logic.
        all.noteType = Constants.NOTE_TYPE_TODO;
        // Initializes todos with repo.query(all).size() so later statements can use the computed value.
        int todos = repo.query(all).size();
        // Stores the result of null in all.noteType for use by later logic.
        all.noteType = null;
        // Stores the result of true in all.pinnedOnly for use by later logic.
        all.pinnedOnly = true;
        // Initializes pinned with repo.query(all).size() so later statements can use the computed value.
        int pinned = repo.query(all).size();
        // Calls b.chipAll.setText with getString(R.string.chip_all_count, total to perform the operation shown.
        b.chipAll.setText(getString(R.string.chip_all_count, total));
        // Calls b.chipNotes.setText with getString(R.string.chip_notes_count, plain to perform the operation shown.
        b.chipNotes.setText(getString(R.string.chip_notes_count, plain));
        // Calls b.chipTodo.setText with getString(R.string.chip_todo_count, todos to perform the operation shown.
        b.chipTodo.setText(getString(R.string.chip_todo_count, todos));
        // Calls b.chipPinned.setText with getString(R.string.chip_pinned_count, pinned to perform the operation shown.
        b.chipPinned.setText(getString(R.string.chip_pinned_count, pinned));

        // Calls adapter.setListStyle with new SettingsRepository(requireContext()).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT to perform the operation shown.
        adapter.setListStyle(new SettingsRepository(requireContext()).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        // Calls adapter.setHighlight with keyword to perform the operation shown.
        adapter.setHighlight(keyword);
        // Calls adapter.setItems with notes to perform the operation shown.
        adapter.setItems(notes);
        // Initializes empty with notes.isEmpty() so later statements can use the computed value.
        boolean empty = notes.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.recycler.setVisibility with empty ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        // Calls b.empty.emptyTitle.setText with R.string.empty_fav_title to perform the operation shown.
        b.empty.emptyTitle.setText(R.string.empty_fav_title);
        // Calls b.empty.emptyMessage.setText with R.string.empty_fav_message to perform the operation shown.
        b.empty.emptyMessage.setText(R.string.empty_fav_message);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override public void onOpen metadata to the declaration that follows.
    @Override public void onOpen(Note note) { NoteActions.open(requireActivity(), note); }

    // Applies @Override public void onFavoriteToggle metadata to the declaration that follows.
    @Override public void onFavoriteToggle(Note note) { NoteActions.toggleFavorite(requireActivity(), note, this::load); }

    // Applies @Override public void onMenu metadata to the declaration that follows.
    @Override public void onMenu(Note note, View anchor) { NoteActions.showMenu(requireActivity(), anchor, note, this::load); }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Releases references tied to the fragment view.
    public void onDestroyView() {
        // Calls super.onDestroyView with  to perform the operation shown.
        super.onDestroyView();
        // Stores the result of null in b for use by later logic.
        b = null;
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
