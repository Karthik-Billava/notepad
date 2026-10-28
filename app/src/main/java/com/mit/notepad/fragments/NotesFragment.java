// Sets the package name to com.mit.notepad.fragments so this class belongs to the app module namespace.
package com.mit.notepad.fragments;

// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.text.Editable so this file can use that type by its short name.
import android.text.Editable;
// Imports android.text.SpannableString so this file can use that type by its short name.
import android.text.SpannableString;
// Imports android.text.Spanned so this file can use that type by its short name.
import android.text.Spanned;
// Imports android.text.TextWatcher so this file can use that type by its short name.
import android.text.TextWatcher;
// Imports android.text.style.ForegroundColorSpan so this file can use that type by its short name.
import android.text.style.ForegroundColorSpan;
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
// Imports com.mit.notepad.activities.SearchActivity so this file can use that type by its short name.
import com.mit.notepad.activities.SearchActivity;
// Imports com.mit.notepad.adapters.NoteAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.NoteAdapter;
// Imports com.mit.notepad.databinding.FragmentNotesBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.FragmentNotesBinding;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Screen 2: Notes list with summary, quick search, filters and sorting. */
// Declares class NotesFragment to group related state and behavior.
public class NotesFragment extends Fragment implements NoteAdapter.Listener {
    // Declares b for use by the surrounding method or class.
    private FragmentNotesBinding b;
    // Declares repo for use by the surrounding method or class.
    private NoteRepository repo;
    // Declares adapter for use by the surrounding method or class.
    private NoteAdapter adapter;
    // Initializes sort with NoteRepository.SORT_NEWEST so later statements can use the computed value.
    private String sort = NoteRepository.SORT_NEWEST;
    // Initializes keyword with the text or query fragment shown here.
    private String keyword = "";

    // Applies @Nullable metadata to the declaration that follows.
    @Nullable
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Builds and returns the fragment view hierarchy.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        // Stores the result of FragmentNotesBinding.inflate(inflater, container, false) in b for use by later logic.
        b = FragmentNotesBinding.inflate(inflater, container, false);
        // Stores the result of new NoteRepository(requireContext()) in repo for use by later logic.
        repo = new NoteRepository(requireContext());

        // Initializes title with new SpannableString(getString(R.string.app_name)) so later statements can use the computed value.
        SpannableString title = new SpannableString(getString(R.string.app_name));
        // Calls title.setSpan with new ForegroundColorSpan(requireContext().getColor(R.color.np_primary)), 4, title.length(), to perform the operation shown.
        title.setSpan(new ForegroundColorSpan(requireContext().getColor(R.color.np_primary)), 4, title.length(),
                // Executes Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) as a statement.
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Calls b.header.headerTitle.setText with title to perform the operation shown.
        b.header.headerTitle.setText(title);
        // Calls b.header.headerSubtitle.setText with R.string.tagline to perform the operation shown.
        b.header.headerSubtitle.setText(R.string.tagline);
        // Calls b.header.headerMore.setVisibility with View.GONE to perform the operation shown.
        b.header.headerMore.setVisibility(View.GONE);
        // Calls b.header.headerNav.setOnClickListener with v -> ((MainActivity) requireActivity()).openDrawer( to perform the operation shown.
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        // Calls b.header.headerSearch.setOnClickListener with v -> startActivity(new Intent(requireContext(), SearchActivity.class to perform the operation shown.
        b.header.headerSearch.setOnClickListener(v -> startActivity(new Intent(requireContext(), SearchActivity.class)));

        // Stores the result of new NoteAdapter(this) in adapter for use by later logic.
        adapter = new NoteAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(requireContext( to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);

        // Calls b.chipGroup.setOnCheckedStateChangeListener with (group, ids) -> load( to perform the operation shown.
        b.chipGroup.setOnCheckedStateChangeListener((group, ids) -> load());
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
        // Calls b.btnSort.setOnClickListener with v -> showSortMenu( to perform the operation shown.
        b.btnSort.setOnClickListener(v -> showSortMenu());
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

    // Displays sort menu.
    private void showSortMenu() {
        // Initializes pm with new PopupMenu(requireContext(), b.btnSort) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(requireContext(), b.btnSort);
        // Calls pm.getMenu with ).add(0, 1, 0, R.string.sort_newest to perform the operation shown.
        pm.getMenu().add(0, 1, 0, R.string.sort_newest);
        // Calls pm.getMenu with ).add(0, 2, 1, R.string.sort_oldest to perform the operation shown.
        pm.getMenu().add(0, 2, 1, R.string.sort_oldest);
        // Calls pm.getMenu with ).add(0, 3, 2, R.string.sort_title to perform the operation shown.
        pm.getMenu().add(0, 3, 2, R.string.sort_title);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // Initializes id with item.getItemId() so later statements can use the computed value.
            int id = item.getItemId();
            // Stores the result of id == 2 ? NoteRepository.SORT_OLDEST : id == 3 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_NEWEST in sort for use by later logic.
            sort = id == 2 ? NoteRepository.SORT_OLDEST : id == 3 ? NoteRepository.SORT_TITLE : NoteRepository.SORT_NEWEST;
            // Calls load with  to perform the operation shown.
            load();
            // Returns true to the caller.
            return true;
        // Executes }) as a statement.
        });
        // Calls pm.show with  to perform the operation shown.
        pm.show();
    // Closes the current method, control-flow, or type block.
    }

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // If b == null is true, return immediately.
        if (b == null) return;
        // Initializes q with new NoteRepository.NoteQuery() so later statements can use the computed value.
        NoteRepository.NoteQuery q = new NoteRepository.NoteQuery();
        // Stores the result of keyword in q.keyword for use by later logic.
        q.keyword = keyword;
        // Stores the result of sort in q.sort for use by later logic.
        q.sort = sort;
        // Stores the result of true in q.pinnedFirst for use by later logic.
        q.pinnedFirst = true; // pinned notes always on top
        // Initializes checked with b.chipGroup.getCheckedChipId() so later statements can use the computed value.
        int checked = b.chipGroup.getCheckedChipId();
        // If checked == R.id.chip_notes is true, q.noteType = Constants.NOTE_TYPE_NOTE immediately.
        if (checked == R.id.chip_notes) q.noteType = Constants.NOTE_TYPE_NOTE;
        // Checks the alternate condition (checked == R.id.chip_todo) after the earlier branch did not run.
        else if (checked == R.id.chip_todo) q.noteType = Constants.NOTE_TYPE_TODO;
        // Checks the alternate condition (checked == R.id.chip_fav) after the earlier branch did not run.
        else if (checked == R.id.chip_fav) q.favoritesOnly = true;

        // Initializes notes with repo.query(q) so later statements can use the computed value.
        List<Note> notes = repo.query(q);
        // Calls adapter.setListStyle with new SettingsRepository(requireContext()).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT to perform the operation shown.
        adapter.setListStyle(new SettingsRepository(requireContext()).get(Constants.SETTING_LIST_STYLE, Constants.LIST_DEFAULT));
        // Calls adapter.setHighlight with keyword to perform the operation shown.
        adapter.setHighlight(keyword);
        // Calls adapter.setItems with notes to perform the operation shown.
        adapter.setItems(notes);

        // Initializes total with repo.count() + repo.countStandalone() so later statements can use the computed value.
        int total = repo.count() + repo.countStandalone();
        // Initializes todos with repo.countByType(Constants.NOTE_TYPE_TODO) so later statements can use the computed value.
        int todos = repo.countByType(Constants.NOTE_TYPE_TODO);
        // Initializes plainNotes with repo.countByType(Constants.NOTE_TYPE_NOTE) so later statements can use the computed value.
        int plainNotes = repo.countByType(Constants.NOTE_TYPE_NOTE);
        // Initializes favs with repo.countFavorites() so later statements can use the computed value.
        int favs = repo.countFavorites();
        // Calls b.tvCountNotes.setText with getString(R.string.count_notes, total to perform the operation shown.
        b.tvCountNotes.setText(getString(R.string.count_notes, total));
        // Calls b.tvCountTodo.setText with getString(R.string.count_todo, todos to perform the operation shown.
        b.tvCountTodo.setText(getString(R.string.count_todo, todos));
        // Calls b.tvCountFav.setText with getString(R.string.count_favourites, favs to perform the operation shown.
        b.tvCountFav.setText(getString(R.string.count_favourites, favs));
        // Calls b.chipAll.setText with getString(R.string.chip_all_count, total to perform the operation shown.
        b.chipAll.setText(getString(R.string.chip_all_count, total));
        // Calls b.chipNotes.setText with getString(R.string.chip_notes_count, plainNotes to perform the operation shown.
        b.chipNotes.setText(getString(R.string.chip_notes_count, plainNotes));
        // Calls b.chipTodo.setText with getString(R.string.chip_todo_count, todos to perform the operation shown.
        b.chipTodo.setText(getString(R.string.chip_todo_count, todos));
        // Calls b.chipFav.setText with getString(R.string.chip_fav_count, favs to perform the operation shown.
        b.chipFav.setText(getString(R.string.chip_fav_count, favs));

        // Initializes empty with notes.isEmpty() so later statements can use the computed value.
        boolean empty = notes.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.recycler.setVisibility with empty ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        // If empty is true, { immediately.
        if (empty) {
            // Initializes filtered with !keyword.trim().isEmpty() || checked != R.id.chip_all so later statements can use the computed value.
            boolean filtered = !keyword.trim().isEmpty() || checked != R.id.chip_all;
            // Calls b.empty.emptyTitle.setText with filtered ? R.string.empty_no_match_title : R.string.empty_notes_title to perform the operation shown.
            b.empty.emptyTitle.setText(filtered ? R.string.empty_no_match_title : R.string.empty_notes_title);
            // Calls b.empty.emptyMessage.setText with filtered ? R.string.empty_no_match_message : R.string.empty_notes_message to perform the operation shown.
            b.empty.emptyMessage.setText(filtered ? R.string.empty_no_match_message : R.string.empty_notes_message);
        // Closes the current method, control-flow, or type block.
        }
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
