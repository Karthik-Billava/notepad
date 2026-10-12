package com.mit.notepad.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mit.notepad.R;
import com.mit.notepad.databinding.ItemTagCardBinding;
import com.mit.notepad.databinding.ItemTagRecentBinding;
import com.mit.notepad.models.Tag;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.TagStyle;

import java.util.ArrayList;
import java.util.List;

/** Shows tags either as coloured grid cards or as "recent tag" rows. */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.Holder> {

    public interface Listener {
        void onOpen(Tag tag);

        void onMenu(Tag tag, View anchor);
    }

    private final List<Tag> items = new ArrayList<>();
    private final boolean recentStyle;
    private final Listener listener;

    public TagAdapter(boolean recentStyle, Listener listener) {
        this.recentStyle = recentStyle;
        this.listener = listener;
    }

    public void setItems(List<Tag> tags) {
        items.clear();
        items.addAll(tags);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (recentStyle) {
            ItemTagRecentBinding b = ItemTagRecentBinding.inflate(inflater, parent, false);
            return new Holder(b.getRoot(), b.iconTile, b.ivIcon, b.tvName, b.tvCount, b.tvUsed, null, null);
        }
        ItemTagCardBinding b = ItemTagCardBinding.inflate(inflater, parent, false);
        return new Holder(b.getRoot(), b.iconTile, b.ivIcon, b.tvName, b.tvCount, null, b.btnMenu, b.card);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Tag t = items.get(position);
        Context ctx = h.root.getContext();
        int color = TagStyle.color(t.color);
        h.name.setText(t.name);
        h.count.setText(ctx.getResources().getQuantityString(R.plurals.notes_count, t.noteCount, t.noteCount));
        h.icon.setImageResource(TagStyle.iconRes(t.icon));
        h.icon.setImageTintList(ColorStateList.valueOf(color));
        h.iconTile.setBackgroundTintList(ColorStateList.valueOf(TagStyle.withAlpha(t.color, 0x33)));
        if (h.card != null) h.card.setCardBackgroundColor(TagStyle.soft(t.color));
        if (h.used != null) {
            h.used.setText(ctx.getString(R.string.used_ago, DateTimeUtils.relative(t.lastUsedAt)));
        }
        h.root.setOnClickListener(v -> listener.onOpen(t));
        if (h.menu != null) h.menu.setOnClickListener(v -> listener.onMenu(t, v));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final View root;
        final FrameLayout iconTile;
        final ImageView icon;
        final TextView name;
        final TextView count;
        final TextView used;
        final View menu;
        final MaterialCardView card;

        Holder(View root, FrameLayout iconTile, ImageView icon, TextView name, TextView count,
               TextView used, View menu, MaterialCardView card) {
            super(root);
            this.root = root;
            this.iconTile = iconTile;
            this.icon = icon;
            this.name = name;
            this.count = count;
            this.used = used;
            this.menu = menu;
            this.card = card;
        }
    }
}
