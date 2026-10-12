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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mit.notepad.R;
import com.mit.notepad.activities.MainActivity;
import com.mit.notepad.activities.TagDialogs;
import com.mit.notepad.activities.TagNotesActivity;
import com.mit.notepad.adapters.TagAdapter;
import com.mit.notepad.databinding.FragmentTagsBinding;
import com.mit.notepad.models.Tag;
import com.mit.notepad.repositories.TagRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Screen 9: coloured tag cards, recent tags, create/rename/delete. */
public class TagsFragment extends Fragment implements TagAdapter.Listener {
    private FragmentTagsBinding b;
    private TagRepository repo;
    private TagAdapter gridAdapter;
    private TagAdapter recentAdapter;
    private String keyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        b = FragmentTagsBinding.inflate(inflater, container, false);
        repo = new TagRepository(requireContext());

        b.header.headerTitle.setText(R.string.title_tags);
        b.header.headerSubtitle.setText(R.string.subtitle_tags);
        b.header.headerSearch.setVisibility(View.GONE);
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        b.header.headerMore.setOnClickListener(v -> {
            PopupMenu pm = new PopupMenu(requireContext(), v);
            pm.getMenu().add(0, 1, 0, R.string.add_new_tag);
            pm.setOnMenuItemClickListener(item -> {
                addTag();
                return true;
            });
            pm.show();
        });

        gridAdapter = new TagAdapter(false, this);
        recentAdapter = new TagAdapter(true, this);
        b.recyclerTags.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        b.recyclerTags.setAdapter(gridAdapter);
        b.recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recyclerRecent.setAdapter(recentAdapter);

        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString().trim().toLowerCase(Locale.getDefault());
                load();
            }
        });
        b.btnAddTag.setOnClickListener(v -> addTag());
        b.fabAdd.setOnClickListener(v -> addTag());
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void addTag() {
        TagDialogs.editTag(requireContext(), null, this::load);
    }

    private void load() {
        if (b == null) return;
        List<Tag> all = repo.getAll();
        List<Tag> shown = new ArrayList<>();
        for (Tag t : all) {
            if (keyword.isEmpty() || t.name.toLowerCase(Locale.getDefault()).contains(keyword)) shown.add(t);
        }
        gridAdapter.setItems(shown);
        b.tvTagTotal.setText(getResources().getQuantityString(R.plurals.tags_count, all.size(), all.size()));
        boolean empty = shown.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.empty.emptyTitle.setText(R.string.empty_tags_title);
        b.empty.emptyMessage.setText(R.string.empty_tags_message);

        List<Tag> recent = repo.getRecent(4);
        recentAdapter.setItems(recent);
        b.recentSection.setVisibility(recent.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onOpen(Tag tag) {
        startActivity(new Intent(requireContext(), TagNotesActivity.class).putExtra(Constants.EXTRA_TAG_ID, tag.id));
    }

    @Override
    public void onMenu(Tag tag, View anchor) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        pm.inflate(R.menu.menu_tag_item);
        pm.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_open) {
                onOpen(tag);
            } else if (id == R.id.action_rename) {
                TagDialogs.editTag(requireContext(), tag, this::load);
            } else if (id == R.id.action_delete) {
                UiUtils.confirm(requireContext(), R.string.delete_tag_title,
                        getString(R.string.delete_tag_message, tag.name), R.string.delete, () -> {
                            repo.delete(tag.id);
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
