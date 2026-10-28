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
// Imports android.view.Menu so this file can use that type by its short name.
import android.view.Menu;
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
// Imports com.mit.notepad.activities.AlarmActivity so this file can use that type by its short name.
import com.mit.notepad.activities.AlarmActivity;
// Imports com.mit.notepad.activities.MainActivity so this file can use that type by its short name.
import com.mit.notepad.activities.MainActivity;
// Imports com.mit.notepad.activities.NoteViewActivity so this file can use that type by its short name.
import com.mit.notepad.activities.NoteViewActivity;
// Imports com.mit.notepad.activities.TodoEditorActivity so this file can use that type by its short name.
import com.mit.notepad.activities.TodoEditorActivity;
// Imports com.mit.notepad.adapters.TodoAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.TodoAdapter;
// Imports com.mit.notepad.databinding.FragmentTodoBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.FragmentTodoBinding;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.TagRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TagRepository;
// Imports com.mit.notepad.repositories.TodoRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TodoRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Screen 6: Pending / Completed tasks with search, filters and overdue highlighting. */
// Declares class TodoFragment to group related state and behavior.
public class TodoFragment extends Fragment implements TodoAdapter.Listener {
    // Initializes FILTER_ALL with 0 so later statements can use the computed value.
    private static final int FILTER_ALL = 0;
    // Initializes FILTER_OVERDUE with 1 so later statements can use the computed value.
    private static final int FILTER_OVERDUE = 1;
    // Initializes FILTER_REMINDER with 2 so later statements can use the computed value.
    private static final int FILTER_REMINDER = 2;
    // Initializes FILTER_TAG with 3 so later statements can use the computed value.
    private static final int FILTER_TAG = 3;

    // Declares b for use by the surrounding method or class.
    private FragmentTodoBinding b;
    // Declares repo for use by the surrounding method or class.
    private TodoRepository repo;
    // Declares adapter for use by the surrounding method or class.
    private TodoAdapter adapter;
    // Initializes keyword with the text or query fragment shown here.
    private String keyword = "";
    // Initializes filterMode with FILTER_ALL so later statements can use the computed value.
    private int filterMode = FILTER_ALL;
    // Declares filterTag for use by the surrounding method or class.
    private Tag filterTag;

    // Applies @Nullable metadata to the declaration that follows.
    @Nullable
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Builds and returns the fragment view hierarchy.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        // Stores the result of FragmentTodoBinding.inflate(inflater, container, false) in b for use by later logic.
        b = FragmentTodoBinding.inflate(inflater, container, false);
        // Stores the result of new TodoRepository(requireContext()) in repo for use by later logic.
        repo = new TodoRepository(requireContext());

        // Calls b.header.headerTitle.setText with R.string.title_todo to perform the operation shown.
        b.header.headerTitle.setText(R.string.title_todo);
        // Calls b.header.headerSubtitle.setText with R.string.subtitle_todo to perform the operation shown.
        b.header.headerSubtitle.setText(R.string.subtitle_todo);
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
        // Calls b.header.headerMore.setOnClickListener with v -> showFilterMenu(v to perform the operation shown.
        b.header.headerMore.setOnClickListener(v -> showFilterMenu(v));

        // Stores the result of new TodoAdapter(this) in adapter for use by later logic.
        adapter = new TodoAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(requireContext( to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);
        // Starts the callback body that handles the event or asynchronous result.
        b.tabGroup.addOnButtonCheckedListener((group, id, checked) -> {
            // If checked is true, load() immediately.
            if (checked) load();
        // Executes }) as a statement.
        });
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
        // Calls b.fabAdd.setOnClickListener with v -> startActivity(new Intent(requireContext(), TodoEditorActivity.class to perform the operation shown.
        b.fabAdd.setOnClickListener(v -> startActivity(new Intent(requireContext(), TodoEditorActivity.class)));
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

    // Displays filter menu.
    private void showFilterMenu(View anchor) {
        // Initializes pm with new PopupMenu(requireContext(), anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        // Calls pm.inflate with R.menu.menu_todo_filter to perform the operation shown.
        pm.inflate(R.menu.menu_todo_filter);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // Initializes id with item.getItemId() so later statements can use the computed value.
            int id = item.getItemId();
            // If id == R.id.filter_all is true, { immediately.
            if (id == R.id.filter_all) {
                // Stores the result of FILTER_ALL in filterMode for use by later logic.
                filterMode = FILTER_ALL;
                // Stores the result of null in filterTag for use by later logic.
                filterTag = null;
            // Checks the alternate condition (id == R.id.filter_overdue) after the earlier branch did not run.
            } else if (id == R.id.filter_overdue) {
                // Stores the result of FILTER_OVERDUE in filterMode for use by later logic.
                filterMode = FILTER_OVERDUE;
            // Checks the alternate condition (id == R.id.filter_reminder) after the earlier branch did not run.
            } else if (id == R.id.filter_reminder) {
                // Stores the result of FILTER_REMINDER in filterMode for use by later logic.
                filterMode = FILTER_REMINDER;
            // Checks the alternate condition (id == R.id.filter_tag) after the earlier branch did not run.
            } else if (id == R.id.filter_tag) {
                // Calls chooseTagFilter with  to perform the operation shown.
                chooseTagFilter();
                // Returns true to the caller.
                return true;
            // Closes the current method, control-flow, or type block.
            }
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

    // Prompts the user to choose tag filter.
    private void chooseTagFilter() {
        // Initializes tags with new TagRepository(requireContext()).getAll() so later statements can use the computed value.
        List<Tag> tags = new TagRepository(requireContext()).getAll();
        // Initializes names with new String[tags.size()] so later statements can use the computed value.
        String[] names = new String[tags.size()];
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < names.length; i++) names[i] = tags.get(i.
        for (int i = 0; i < names.length; i++) names[i] = tags.get(i).name;
        // Creates the object described by new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext()).
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.filter_by_tag)
                // Starts the callback body that handles the event or asynchronous result.
                .setItems(names, (d, which) -> {
                    // Stores the result of FILTER_TAG in filterMode for use by later logic.
                    filterMode = FILTER_TAG;
                    // Stores the result of tags.get(which) in filterTag for use by later logic.
                    filterTag = tags.get(which);
                    // Calls load with  to perform the operation shown.
                    load();
                // Continues the expression above with }).
                })
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // If b == null is true, return immediately.
        if (b == null) return;
        // Initializes completedTab with b.tabGroup.getCheckedButtonId() == R.id.btn_completed so later statements can use the computed value.
        boolean completedTab = b.tabGroup.getCheckedButtonId() == R.id.btn_completed;
        // Initializes q with new TodoRepository.TodoQuery() so later statements can use the computed value.
        TodoRepository.TodoQuery q = new TodoRepository.TodoQuery();
        // Stores the result of completedTab ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING in q.status for use by later logic.
        q.status = completedTab ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING;
        // Stores the result of keyword in q.keyword for use by later logic.
        q.keyword = keyword;
        // Stores the result of filterMode == FILTER_OVERDUE in q.overdueOnly for use by later logic.
        q.overdueOnly = filterMode == FILTER_OVERDUE;
        // Stores the result of filterMode == FILTER_REMINDER in q.reminderOnly for use by later logic.
        q.reminderOnly = filterMode == FILTER_REMINDER;
        // If filterMode == FILTER_TAG && filterTag != null is true, q.tagId = filterTag.id immediately.
        if (filterMode == FILTER_TAG && filterTag != null) q.tagId = filterTag.id;
        // Initializes list with repo.query(q) so later statements can use the computed value.
        List<Todo> list = repo.query(q);
        // Calls adapter.setItems with list to perform the operation shown.
        adapter.setItems(list);

        // Calls b.btnPending.setText with getString(R.string.tab_pending, repo.count(Constants.STATUS_PENDING to perform the operation shown.
        b.btnPending.setText(getString(R.string.tab_pending, repo.count(Constants.STATUS_PENDING)));
        // Calls b.btnCompleted.setText with getString(R.string.tab_completed, repo.count(Constants.STATUS_COMPLETED to perform the operation shown.
        b.btnCompleted.setText(getString(R.string.tab_completed, repo.count(Constants.STATUS_COMPLETED)));

        // If filterMode == FILTER_ALL is true, { immediately.
        if (filterMode == FILTER_ALL) {
            // Calls b.tvFilterLabel.setVisibility with View.GONE to perform the operation shown.
            b.tvFilterLabel.setVisibility(View.GONE);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls b.tvFilterLabel.setVisibility with View.VISIBLE to perform the operation shown.
            b.tvFilterLabel.setVisibility(View.VISIBLE);
            // Initializes label with filterMode == FILTER_OVERDUE ? getString(R.string.filter_overdue) so later statements can use the computed value.
            String label = filterMode == FILTER_OVERDUE ? getString(R.string.filter_overdue)
                    // Calls getString with R.string.filter_with_reminder to perform the operation shown.
                    : filterMode == FILTER_REMINDER ? getString(R.string.filter_with_reminder)
                    // Calls getString with R.string.filter_tag_label, filterTag == null ? "" : filterTag.name to perform the operation shown.
                    : getString(R.string.filter_tag_label, filterTag == null ? "" : filterTag.name);
            // Calls b.tvFilterLabel.setText with getString(R.string.filter_active, label to perform the operation shown.
            b.tvFilterLabel.setText(getString(R.string.filter_active, label));
        // Closes the current method, control-flow, or type block.
        }

        // Initializes empty with list.isEmpty() so later statements can use the computed value.
        boolean empty = list.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.recycler.setVisibility with empty ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        // Calls b.empty.emptyTitle.setText with completedTab ? R.string.empty_completed_title : R.string.empty_pending_title to perform the operation shown.
        b.empty.emptyTitle.setText(completedTab ? R.string.empty_completed_title : R.string.empty_pending_title);
        // Calls b.empty.emptyMessage.setText with completedTab ? R.string.empty_completed_message : R.string.empty_pending_message to perform the operation shown.
        b.empty.emptyMessage.setText(completedTab ? R.string.empty_completed_message : R.string.empty_pending_message);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onToggle() to implement this part of the component.
    public void onToggle(Todo todo, boolean completed) {
        // Calls repo.setCompleted with todo.id, completed to perform the operation shown.
        repo.setCompleted(todo.id, completed);
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onOpen() to implement this part of the component.
    public void onOpen(Todo todo) {
        // If todo.isLinkedToNote() is true, { immediately.
        if (todo.isLinkedToNote()) {
            // Calls startActivity with new Intent(requireContext(), NoteViewActivity.class to perform the operation shown.
            startActivity(new Intent(requireContext(), NoteViewActivity.class)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_NOTE_ID, todo.noteId));
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls startActivity with new Intent(requireContext(), TodoEditorActivity.class to perform the operation shown.
            startActivity(new Intent(requireContext(), TodoEditorActivity.class)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_TODO_ID, todo.id));
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onMenu() to implement this part of the component.
    public void onMenu(Todo todo, View anchor) {
        // Initializes pm with new PopupMenu(requireContext(), anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        // Calls pm.inflate with R.menu.menu_todo_item to perform the operation shown.
        pm.inflate(R.menu.menu_todo_item);
        // Initializes m with pm.getMenu() so later statements can use the computed value.
        Menu m = pm.getMenu();
        // Calls m.findItem with R.id.action_open_note).setVisible(todo.isLinkedToNote( to perform the operation shown.
        m.findItem(R.id.action_open_note).setVisible(todo.isLinkedToNote());
        // Calls m.findItem with R.id.action_toggle).setTitle(todo.completed ? R.string.mark_pending : R.string.mark_completed to perform the operation shown.
        m.findItem(R.id.action_toggle).setTitle(todo.completed ? R.string.mark_pending : R.string.mark_completed);
        // Calls m.findItem with R.id.action_delete).setTitle(todo.isLinkedToNote() ? R.string.remove_from_todo : R.string.delete to perform the operation shown.
        m.findItem(R.id.action_delete).setTitle(todo.isLinkedToNote() ? R.string.remove_from_todo : R.string.delete);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // Initializes id with item.getItemId() so later statements can use the computed value.
            int id = item.getItemId();
            // If id == R.id.action_edit is true, { immediately.
            if (id == R.id.action_edit) {
                // If todo.isLinkedToNote() is true, { immediately.
                if (todo.isLinkedToNote()) {
                    // Calls startActivity with new Intent(requireContext(), to perform the operation shown.
                    startActivity(new Intent(requireContext(),
                            // Continues the expression above with com.mit.notepad.activities.NoteEditorActivity.class).
                            com.mit.notepad.activities.NoteEditorActivity.class)
                            // Calls putExtra on the object from the preceding line with the shown arguments.
                            .putExtra(Constants.EXTRA_NOTE_ID, todo.noteId));
                // Runs the fallback branch when the preceding condition did not match.
                } else {
                    // Calls onOpen with todo to perform the operation shown.
                    onOpen(todo);
                // Closes the current method, control-flow, or type block.
                }
            // Checks the alternate condition (id == R.id.action_open_note) after the earlier branch did not run.
            } else if (id == R.id.action_open_note) {
                // Calls onOpen with todo to perform the operation shown.
                onOpen(todo);
            // Checks the alternate condition (id == R.id.action_toggle) after the earlier branch did not run.
            } else if (id == R.id.action_toggle) {
                // Calls onToggle with todo, !todo.completed to perform the operation shown.
                onToggle(todo, !todo.completed);
            // Checks the alternate condition (id == R.id.action_alarm) after the earlier branch did not run.
            } else if (id == R.id.action_alarm) {
                // Initializes i with new Intent(requireContext(), AlarmActivity.class) so later statements can use the computed value.
                Intent i = new Intent(requireContext(), AlarmActivity.class);
                // If todo.isLinkedToNote() is true, i.putExtra(Constants.EXTRA_NOTE_ID, todo.noteId) immediately.
                if (todo.isLinkedToNote()) i.putExtra(Constants.EXTRA_NOTE_ID, todo.noteId);
                // Runs the fallback branch when the preceding condition did not match.
                else i.putExtra(Constants.EXTRA_TODO_ID, todo.id);
                // Calls startActivity with i to perform the operation shown.
                startActivity(i);
            // Checks the alternate condition (id == R.id.action_delete) after the earlier branch did not run.
            } else if (id == R.id.action_delete) {
                // Initializes msg with todo.isLinkedToNote() ? R.string.remove_from_todo_message : R.string.delete_task_message so later statements can use the computed value.
                int msg = todo.isLinkedToNote() ? R.string.remove_from_todo_message : R.string.delete_task_message;
                // Calls UiUtils.confirm with requireContext(), R.string.delete_task_title, to perform the operation shown.
                UiUtils.confirm(requireContext(), R.string.delete_task_title,
                        // Starts the callback body that handles the event or asynchronous result.
                        getString(msg, todo.title), todo.isLinkedToNote() ? R.string.remove : R.string.delete, () -> {
                            // If todo.isLinkedToNote() is true, { immediately.
                            if (todo.isLinkedToNote()) {
                                // Creates the object described by new NoteRepository(requireContext()).setNoteType(todo.noteId, Constants.NOTE_TYPE_NOTE).
                                new NoteRepository(requireContext()).setNoteType(todo.noteId, Constants.NOTE_TYPE_NOTE);
                            // Runs the fallback branch when the preceding condition did not match.
                            } else {
                                // Calls repo.delete with todo.id to perform the operation shown.
                                repo.delete(todo.id);
                            // Closes the current method, control-flow, or type block.
                            }
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
