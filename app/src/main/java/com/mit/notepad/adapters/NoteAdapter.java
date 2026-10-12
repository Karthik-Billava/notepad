package com.mit.notepad.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.notepad.R;
import com.mit.notepad.databinding.ItemNoteBinding;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Tag;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.TagStyle;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/** Note cards used by Notes, Favourites, Search and Tag screens. Honors the list-view theme. */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {

    public interface Listener {
        void onOpen(Note note);

        void onFavoriteToggle(Note note);

        void onMenu(Note note, View anchor);
    }

    private final List<Note> items = new ArrayList<>();
    private final Listener listener;
    private String listStyle = Constants.LIST_DEFAULT;
    private String highlight = "";

    public NoteAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Note> notes) {
        items.clear();
        items.addAll(notes);
        notifyDataSetChanged();
    }

    public void setListStyle(String style) {
        this.listStyle = style;
    }

    public void setHighlight(String keyword) {
        this.highlight = keyword == null ? "" : keyword;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Note n = items.get(position);
        ItemNoteBinding b = h.b;
        Context ctx = b.getRoot().getContext();
        boolean compact = Constants.LIST_COMPACT.equals(listStyle);
        boolean detailed = Constants.LIST_DETAILED.equals(listStyle);
        int highlightColor = ctx.getColor(R.color.np_primary_light);

        b.tvTitle.setText(UiUtils.highlight(n.title, highlight, highlightColor));
        b.tvPreview.setText(UiUtils.highlight(n.content, highlight, highlightColor));
        b.tvPreview.setMaxLines(compact ? 1 : (detailed ? 5 : 2));
        b.tvPreview.setVisibility(n.content.isEmpty() ? View.GONE : View.VISIBLE);

        int pad = ctx.getResources().getDimensionPixelSize(compact ? R.dimen.np_item_padding_compact
                : detailed ? R.dimen.np_item_padding_detailed : R.dimen.np_item_padding_default);
        b.contentRow.setPadding(pad, pad, pad, pad);

        // Category icon: first tag's icon/colour, otherwise note-type icon
        int iconRes;
        int color;
        if (!n.tags.isEmpty()) {
            Tag t = n.tags.get(0);
            iconRes = TagStyle.iconRes(t.icon);
            color = TagStyle.color(t.color);
        } else {
            iconRes = n.isTodo() ? R.drawable.ic_todo : R.drawable.ic_doc;
            color = ctx.getColor(R.color.np_primary);
        }
        b.ivIcon.setImageResource(iconRes);
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf(TagStyle.soft(hex(color))));

        b.btnFav.setImageResource(n.favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
        b.btnFav.setImageTintList(ColorStateList.valueOf(ctx.getColor(
                n.favorite ? R.color.np_star : R.color.np_text_secondary)));
        b.ivPin.setVisibility(n.pinned ? View.VISIBLE : View.GONE);

        b.tvDate.setText(DateTimeUtils.formatDateTime(n.updatedAt));
        if (compact) {
            b.tvStats.setVisibility(View.GONE);
        } else {
            b.tvStats.setVisibility(View.VISIBLE);
            b.tvStats.setText(ctx.getString(R.string.words_chars_short, n.wordCount, n.charCount));
        }

        b.llTags.removeAllViews();
        if (detailed && !n.tags.isEmpty()) {
            b.llTags.setVisibility(View.VISIBLE);
            for (Tag t : n.tags) {
                View pill = UiUtils.tagPill(ctx, t);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMarginEnd(UiUtils.dp(ctx, 6));
                b.llTags.addView(pill, lp);
            }
        } else {
            b.llTags.setVisibility(View.GONE);
        }

        b.getRoot().setOnClickListener(v -> listener.onOpen(n));
        b.btnFav.setOnClickListener(v -> listener.onFavoriteToggle(n));
        b.btnMenu.setOnClickListener(v -> listener.onMenu(n, v));
    }

    private static String hex(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemNoteBinding b;

        Holder(ItemNoteBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
