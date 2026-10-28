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
// Imports androidx.recyclerview.widget.GridLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.GridLayoutManager;
// Imports androidx.recyclerview.widget.LinearLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.LinearLayoutManager;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.activities.MainActivity so this file can use that type by its short name.
import com.mit.notepad.activities.MainActivity;
// Imports com.mit.notepad.activities.TagDialogs so this file can use that type by its short name.
import com.mit.notepad.activities.TagDialogs;
// Imports com.mit.notepad.activities.TagNotesActivity so this file can use that type by its short name.
import com.mit.notepad.activities.TagNotesActivity;
// Imports com.mit.notepad.adapters.TagAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.TagAdapter;
// Imports com.mit.notepad.databinding.FragmentTagsBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.FragmentTagsBinding;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.repositories.TagRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TagRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Locale so this file can use that type by its short name.
import java.util.Locale;

/** Screen 9: coloured tag cards, recent tags, create/rename/delete. */
// Declares class TagsFragment to group related state and behavior.
public class TagsFragment extends Fragment implements TagAdapter.Listener {
    // Declares b for use by the surrounding method or class.
    private FragmentTagsBinding b;
    // Declares repo for use by the surrounding method or class.
    private TagRepository repo;
    // Declares gridAdapter for use by the surrounding method or class.
    private TagAdapter gridAdapter;
    // Declares recentAdapter for use by the surrounding method or class.
    private TagAdapter recentAdapter;
    // Initializes keyword with the text or query fragment shown here.
    private String keyword = "";

    // Applies @Nullable metadata to the declaration that follows.
    @Nullable
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Builds and returns the fragment view hierarchy.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        // Stores the result of FragmentTagsBinding.inflate(inflater, container, false) in b for use by later logic.
        b = FragmentTagsBinding.inflate(inflater, container, false);
        // Stores the result of new TagRepository(requireContext()) in repo for use by later logic.
        repo = new TagRepository(requireContext());

        // Calls b.header.headerTitle.setText with R.string.title_tags to perform the operation shown.
        b.header.headerTitle.setText(R.string.title_tags);
        // Calls b.header.headerSubtitle.setText with R.string.subtitle_tags to perform the operation shown.
        b.header.headerSubtitle.setText(R.string.subtitle_tags);
        // Calls b.header.headerSearch.setVisibility with View.GONE to perform the operation shown.
        b.header.headerSearch.setVisibility(View.GONE);
        // Calls b.header.headerNav.setOnClickListener with v -> ((MainActivity) requireActivity()).openDrawer( to perform the operation shown.
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        // Starts the callback body that handles the event or asynchronous result.
        b.header.headerMore.setOnClickListener(v -> {
            // Initializes pm with new PopupMenu(requireContext(), v) so later statements can use the computed value.
            PopupMenu pm = new PopupMenu(requireContext(), v);
            // Calls pm.getMenu with ).add(0, 1, 0, R.string.add_new_tag to perform the operation shown.
            pm.getMenu().add(0, 1, 0, R.string.add_new_tag);
            // Starts the callback body that handles the event or asynchronous result.
            pm.setOnMenuItemClickListener(item -> {
                // Calls addTag with  to perform the operation shown.
                addTag();
                // Returns true to the caller.
                return true;
            // Executes }) as a statement.
            });
            // Calls pm.show with  to perform the operation shown.
            pm.show();
        // Executes }) as a statement.
        });

        // Stores the result of new TagAdapter(false, this) in gridAdapter for use by later logic.
        gridAdapter = new TagAdapter(false, this);
        // Stores the result of new TagAdapter(true, this) in recentAdapter for use by later logic.
        recentAdapter = new TagAdapter(true, this);
        // Calls b.recyclerTags.setLayoutManager with new GridLayoutManager(requireContext(), 2 to perform the operation shown.
        b.recyclerTags.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        // Calls b.recyclerTags.setAdapter with gridAdapter to perform the operation shown.
        b.recyclerTags.setAdapter(gridAdapter);
        // Calls b.recyclerRecent.setLayoutManager with new LinearLayoutManager(requireContext( to perform the operation shown.
        b.recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Calls b.recyclerRecent.setAdapter with recentAdapter to perform the operation shown.
        b.recyclerRecent.setAdapter(recentAdapter);

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
                // Stores the result of s.toString().trim().toLowerCase(Locale.getDefault()) in keyword for use by later logic.
                keyword = s.toString().trim().toLowerCase(Locale.getDefault());
                // Calls load with  to perform the operation shown.
                load();
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Calls b.btnAddTag.setOnClickListener with v -> addTag( to perform the operation shown.
        b.btnAddTag.setOnClickListener(v -> addTag());
        // Calls b.fabAdd.setOnClickListener with v -> addTag( to perform the operation shown.
        b.fabAdd.setOnClickListener(v -> addTag());
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

    // Adds tag.
    private void addTag() {
        // Calls TagDialogs.editTag with requireContext(), null, this::load to perform the operation shown.
        TagDialogs.editTag(requireContext(), null, this::load);
    // Closes the current method, control-flow, or type block.
    }

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // If b == null is true, return immediately.
        if (b == null) return;
        // Initializes all with repo.getAll() so later statements can use the computed value.
        List<Tag> all = repo.getAll();
        // Initializes shown with new ArrayList<>() so later statements can use the computed value.
        List<Tag> shown = new ArrayList<>();
        // Repeats the following block according to the initialization, condition, and update in Tag t : all.
        for (Tag t : all) {
            // If keyword.isEmpty() || t.name.toLowerCase(Locale.getDefault()).contains(keyword) is true, shown.add(t) immediately.
            if (keyword.isEmpty() || t.name.toLowerCase(Locale.getDefault()).contains(keyword)) shown.add(t);
        // Closes the current method, control-flow, or type block.
        }
        // Calls gridAdapter.setItems with shown to perform the operation shown.
        gridAdapter.setItems(shown);
        // Calls b.tvTagTotal.setText with getResources().getQuantityString(R.plurals.tags_count, all.size(), all.size( to perform the operation shown.
        b.tvTagTotal.setText(getResources().getQuantityString(R.plurals.tags_count, all.size(), all.size()));
        // Initializes empty with shown.isEmpty() so later statements can use the computed value.
        boolean empty = shown.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.empty.emptyTitle.setText with R.string.empty_tags_title to perform the operation shown.
        b.empty.emptyTitle.setText(R.string.empty_tags_title);
        // Calls b.empty.emptyMessage.setText with R.string.empty_tags_message to perform the operation shown.
        b.empty.emptyMessage.setText(R.string.empty_tags_message);

        // Initializes recent with repo.getRecent(4) so later statements can use the computed value.
        List<Tag> recent = repo.getRecent(4);
        // Calls recentAdapter.setItems with recent to perform the operation shown.
        recentAdapter.setItems(recent);
        // Calls b.recentSection.setVisibility with recent.isEmpty() ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recentSection.setVisibility(recent.isEmpty() ? View.GONE : View.VISIBLE);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onOpen() to implement this part of the component.
    public void onOpen(Tag tag) {
        // Calls startActivity with new Intent(requireContext(), TagNotesActivity.class).putExtra(Constants.EXTRA_TAG_ID, tag.id to perform the operation shown.
        startActivity(new Intent(requireContext(), TagNotesActivity.class).putExtra(Constants.EXTRA_TAG_ID, tag.id));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onMenu() to implement this part of the component.
    public void onMenu(Tag tag, View anchor) {
        // Initializes pm with new PopupMenu(requireContext(), anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        // Calls pm.inflate with R.menu.menu_tag_item to perform the operation shown.
        pm.inflate(R.menu.menu_tag_item);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // Initializes id with item.getItemId() so later statements can use the computed value.
            int id = item.getItemId();
            // If id == R.id.action_open is true, { immediately.
            if (id == R.id.action_open) {
                // Calls onOpen with tag to perform the operation shown.
                onOpen(tag);
            // Checks the alternate condition (id == R.id.action_rename) after the earlier branch did not run.
            } else if (id == R.id.action_rename) {
                // Calls TagDialogs.editTag with requireContext(), tag, this::load to perform the operation shown.
                TagDialogs.editTag(requireContext(), tag, this::load);
            // Checks the alternate condition (id == R.id.action_delete) after the earlier branch did not run.
            } else if (id == R.id.action_delete) {
                // Calls UiUtils.confirm with requireContext(), R.string.delete_tag_title, to perform the operation shown.
                UiUtils.confirm(requireContext(), R.string.delete_tag_title,
                        // Starts the callback body that handles the event or asynchronous result.
                        getString(R.string.delete_tag_message, tag.name), R.string.delete, () -> {
                            // Calls repo.delete with tag.id to perform the operation shown.
                            repo.delete(tag.id);
                            // Calls load with  to perform the operation shown.
                            load();
                        // Executes }) as a statement.
                        });
            // Closes the current method, control-flow, or type block.
            }
            // Returns true to the caller.
            return true;
        // Executes }) as a statement.
        });
        // Calls pm.show with  to perform the operation shown.
        pm.show();
    // Closes the current method, control-flow, or type block.
    }

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
