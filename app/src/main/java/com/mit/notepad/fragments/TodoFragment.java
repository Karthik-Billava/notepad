package com.mit.notepad.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mit.notepad.R;
import com.mit.notepad.activities.AlarmActivity;
import com.mit.notepad.activities.MainActivity;
import com.mit.notepad.activities.NoteViewActivity;
import com.mit.notepad.activities.TodoEditorActivity;
import com.mit.notepad.adapters.TodoAdapter;
import com.mit.notepad.databinding.FragmentTodoBinding;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.TagRepository;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.UiUtils;

import java.util.List;

/** Screen 6: Pending / Completed tasks with search, filters and overdue highlighting. */
public class TodoFragment extends Fragment implements TodoAdapter.Listener {
    private static final int FILTER_ALL = 0;
    private static final int FILTER_OVERDUE = 1;
    private static final int FILTER_REMINDER = 2;
    private static final int FILTER_TAG = 3;

    private FragmentTodoBinding b;
    private TodoRepository repo;
    private TodoAdapter adapter;
    private String keyword = "";
    private int filterMode = FILTER_ALL;
    private Tag filterTag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        b = FragmentTodoBinding.inflate(inflater, container, false);
        repo = new TodoRepository(requireContext());

        b.header.headerTitle.setText(R.string.title_todo);
        b.header.headerSubtitle.setText(R.string.subtitle_todo);
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
        b.header.headerMore.setOnClickListener(v -> showFilterMenu(v));

        adapter = new TodoAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recycler.setAdapter(adapter);
        b.tabGroup.addOnButtonCheckedListener((group, id, checked) -> {
            if (checked) load();
        });
        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                load();
            }
        });
        b.fabAdd.setOnClickListener(v -> startActivity(new Intent(requireContext(), TodoEditorActivity.class)));
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void showFilterMenu(View anchor) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        pm.inflate(R.menu.menu_todo_filter);
        pm.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.filter_all) {
                filterMode = FILTER_ALL;
                filterTag = null;
            } else if (id == R.id.filter_overdue) {
                filterMode = FILTER_OVERDUE;
            } else if (id == R.id.filter_reminder) {
                filterMode = FILTER_REMINDER;
            } else if (id == R.id.filter_tag) {
                chooseTagFilter();
                return true;
            }
            load();
            return true;
        });
        pm.show();
    }

    private void chooseTagFilter() {
        List<Tag> tags = new TagRepository(requireContext()).getAll();
        String[] names = new String[tags.size()];
        for (int i = 0; i < names.length; i++) names[i] = tags.get(i).name;
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.filter_by_tag)
                .setItems(names, (d, which) -> {
                    filterMode = FILTER_TAG;
                    filterTag = tags.get(which);
                    load();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void load() {
        if (b == null) return;
        boolean completedTab = b.tabGroup.getCheckedButtonId() == R.id.btn_completed;
        TodoRepository.TodoQuery q = new TodoRepository.TodoQuery();
        q.status = completedTab ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING;
        q.keyword = keyword;
        q.overdueOnly = filterMode == FILTER_OVERDUE;
        q.reminderOnly = filterMode == FILTER_REMINDER;
        if (filterMode == FILTER_TAG && filterTag != null) q.tagId = filterTag.id;
        List<Todo> list = repo.query(q);
        adapter.setItems(list);

        b.btnPending.setText(getString(R.string.tab_pending, repo.count(Constants.STATUS_PENDING)));
        b.btnCompleted.setText(getString(R.string.tab_completed, repo.count(Constants.STATUS_COMPLETED)));

        if (filterMode == FILTER_ALL) {
            b.tvFilterLabel.setVisibility(View.GONE);
        } else {
            b.tvFilterLabel.setVisibility(View.VISIBLE);
            String label = filterMode == FILTER_OVERDUE ? getString(R.string.filter_overdue)
                    : filterMode == FILTER_REMINDER ? getString(R.string.filter_with_reminder)
                    : getString(R.string.filter_tag_label, filterTag == null ? "" : filterTag.name);
            b.tvFilterLabel.setText(getString(R.string.filter_active, label));
        }

        boolean empty = list.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.empty.emptyTitle.setText(completedTab ? R.string.empty_completed_title : R.string.empty_pending_title);
        b.empty.emptyMessage.setText(completedTab ? R.string.empty_completed_message : R.string.empty_pending_message);
    }

    @Override
    public void onToggle(Todo todo, boolean completed) {
        repo.setCompleted(todo.id, completed);
        load();
    }

    @Override
    public void onOpen(Todo todo) {
        if (todo.isLinkedToNote()) {
            startActivity(new Intent(requireContext(), NoteViewActivity.class)
                    .putExtra(Constants.EXTRA_NOTE_ID, todo.noteId));
        } else {
            startActivity(new Intent(requireContext(), TodoEditorActivity.class)
                    .putExtra(Constants.EXTRA_TODO_ID, todo.id));
        }
    }

    @Override
    public void onMenu(Todo todo, View anchor) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        pm.inflate(R.menu.menu_todo_item);
        Menu m = pm.getMenu();
        m.findItem(R.id.action_open_note).setVisible(todo.isLinkedToNote());
        m.findItem(R.id.action_toggle).setTitle(todo.completed ? R.string.mark_pending : R.string.mark_completed);
        m.findItem(R.id.action_delete).setTitle(todo.isLinkedToNote() ? R.string.remove_from_todo : R.string.delete);
        pm.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_edit) {
                if (todo.isLinkedToNote()) {
                    startActivity(new Intent(requireContext(),
                            com.mit.notepad.activities.NoteEditorActivity.class)
                            .putExtra(Constants.EXTRA_NOTE_ID, todo.noteId));
                } else {
                    onOpen(todo);
                }
            } else if (id == R.id.action_open_note) {
                onOpen(todo);
            } else if (id == R.id.action_toggle) {
                onToggle(todo, !todo.completed);
            } else if (id == R.id.action_alarm) {
                Intent i = new Intent(requireContext(), AlarmActivity.class);
                if (todo.isLinkedToNote()) i.putExtra(Constants.EXTRA_NOTE_ID, todo.noteId);
                else i.putExtra(Constants.EXTRA_TODO_ID, todo.id);
                startActivity(i);
            } else if (id == R.id.action_delete) {
                int msg = todo.isLinkedToNote() ? R.string.remove_from_todo_message : R.string.delete_task_message;
                UiUtils.confirm(requireContext(), R.string.delete_task_title,
                        getString(msg, todo.title), todo.isLinkedToNote() ? R.string.remove : R.string.delete, () -> {
                            if (todo.isLinkedToNote()) {
                                new NoteRepository(requireContext()).setNoteType(todo.noteId, Constants.NOTE_TYPE_NOTE);
                            } else {
                                repo.delete(todo.id);
                            }
                            load();
                        });
            }
            return true;
        });
        pm.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
