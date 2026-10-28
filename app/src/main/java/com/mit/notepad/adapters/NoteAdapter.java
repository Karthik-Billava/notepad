// Sets the package name to com.mit.notepad.adapters so this class belongs to the app module namespace.
package com.mit.notepad.adapters;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.res.ColorStateList so this file can use that type by its short name.
import android.content.res.ColorStateList;
// Imports android.view.LayoutInflater so this file can use that type by its short name.
import android.view.LayoutInflater;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.view.ViewGroup so this file can use that type by its short name.
import android.view.ViewGroup;
// Imports android.widget.LinearLayout so this file can use that type by its short name.
import android.widget.LinearLayout;

// Imports androidx.annotation.NonNull so this file can use that type by its short name.
import androidx.annotation.NonNull;
// Imports androidx.recyclerview.widget.RecyclerView so this file can use that type by its short name.
import androidx.recyclerview.widget.RecyclerView;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ItemNoteBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ItemNoteBinding;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.TagStyle so this file can use that type by its short name.
import com.mit.notepad.utils.TagStyle;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Note cards used by Notes, Favourites, Search and Tag screens. Honors the list-view theme. */
// Declares class NoteAdapter to group related state and behavior.
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {

    // Declares interface Listener to group related state and behavior.
    public interface Listener {
        // Calls onOpen with Note note to perform the operation shown.
        void onOpen(Note note);

        // Calls onFavoriteToggle with Note note to perform the operation shown.
        void onFavoriteToggle(Note note);

        // Calls onMenu with Note note, View anchor to perform the operation shown.
        void onMenu(Note note, View anchor);
    // Closes the current method, control-flow, or type block.
    }

    // Initializes items with new ArrayList<>() so later statements can use the computed value.
    private final List<Note> items = new ArrayList<>();
    // Declares listener for use by the surrounding method or class.
    private final Listener listener;
    // Initializes listStyle with Constants.LIST_DEFAULT so later statements can use the computed value.
    private String listStyle = Constants.LIST_DEFAULT;
    // Initializes highlight with the text or query fragment shown here.
    private String highlight = "";

    // Declares NoteAdapter() to implement this part of the component.
    public NoteAdapter(Listener listener) {
        // Stores the result of listener in this.listener for use by later logic.
        this.listener = listener;
    // Closes the current method, control-flow, or type block.
    }

    // Updates items.
    public void setItems(List<Note> notes) {
        // Calls items.clear with  to perform the operation shown.
        items.clear();
        // Calls items.addAll with notes to perform the operation shown.
        items.addAll(notes);
        // Calls notifyDataSetChanged with  to perform the operation shown.
        notifyDataSetChanged();
    // Closes the current method, control-flow, or type block.
    }

    // Updates list style.
    public void setListStyle(String style) {
        // Stores the result of style in this.listStyle for use by later logic.
        this.listStyle = style;
    // Closes the current method, control-flow, or type block.
    }

    // Updates highlight.
    public void setHighlight(String keyword) {
        // Stores the result of keyword == null ? "" : keyword in this.highlight for use by later logic.
        this.highlight = keyword == null ? "" : keyword;
    // Closes the current method, control-flow, or type block.
    }

    // Applies @NonNull metadata to the declaration that follows.
    @NonNull
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Inflates and wraps a RecyclerView row layout.
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Returns new Holder(ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)) to the caller.
        return new Holder(ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Fills the RecyclerView row with the item at the requested position.
    public void onBindViewHolder(@NonNull Holder h, int position) {
        // Initializes n with items.get(position) so later statements can use the computed value.
        Note n = items.get(position);
        // Initializes b with h.b so later statements can use the computed value.
        ItemNoteBinding b = h.b;
        // Initializes ctx with b.getRoot().getContext() so later statements can use the computed value.
        Context ctx = b.getRoot().getContext();
        // Initializes compact with Constants.LIST_COMPACT.equals(listStyle) so later statements can use the computed value.
        boolean compact = Constants.LIST_COMPACT.equals(listStyle);
        // Initializes detailed with Constants.LIST_DETAILED.equals(listStyle) so later statements can use the computed value.
        boolean detailed = Constants.LIST_DETAILED.equals(listStyle);
        // Initializes highlightColor with ctx.getColor(R.color.np_primary_light) so later statements can use the computed value.
        int highlightColor = ctx.getColor(R.color.np_primary_light);

        // Calls b.tvTitle.setText with UiUtils.highlight(n.title, highlight, highlightColor to perform the operation shown.
        b.tvTitle.setText(UiUtils.highlight(n.title, highlight, highlightColor));
        // Calls b.tvPreview.setText with UiUtils.highlight(n.content, highlight, highlightColor to perform the operation shown.
        b.tvPreview.setText(UiUtils.highlight(n.content, highlight, highlightColor));
        // Calls b.tvPreview.setMaxLines with compact ? 1 : (detailed ? 5 : 2 to perform the operation shown.
        b.tvPreview.setMaxLines(compact ? 1 : (detailed ? 5 : 2));
        // Calls b.tvPreview.setVisibility with n.content.isEmpty() ? View.GONE : View.VISIBLE to perform the operation shown.
        b.tvPreview.setVisibility(n.content.isEmpty() ? View.GONE : View.VISIBLE);

        // Initializes pad with ctx.getResources().getDimensionPixelSize(compact ? R.dimen.np_item_padding_compact so later statements can use the computed value.
        int pad = ctx.getResources().getDimensionPixelSize(compact ? R.dimen.np_item_padding_compact
                // Executes : detailed ? R.dimen.np_item_padding_detailed : R.dimen.np_item_padding_default) as a statement.
                : detailed ? R.dimen.np_item_padding_detailed : R.dimen.np_item_padding_default);
        // Calls b.contentRow.setPadding with pad, pad, pad, pad to perform the operation shown.
        b.contentRow.setPadding(pad, pad, pad, pad);

        // Category icon: first tag's icon/colour, otherwise note-type icon
        // Declares iconRes for use by the surrounding method or class.
        int iconRes;
        // Declares color for use by the surrounding method or class.
        int color;
        // If !n.tags.isEmpty() is true, { immediately.
        if (!n.tags.isEmpty()) {
            // Initializes t with n.tags.get(0) so later statements can use the computed value.
            Tag t = n.tags.get(0);
            // Stores the result of TagStyle.iconRes(t.icon) in iconRes for use by later logic.
            iconRes = TagStyle.iconRes(t.icon);
            // Stores the result of TagStyle.color(t.color) in color for use by later logic.
            color = TagStyle.color(t.color);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of n.isTodo() ? R.drawable.ic_todo : R.drawable.ic_doc in iconRes for use by later logic.
            iconRes = n.isTodo() ? R.drawable.ic_todo : R.drawable.ic_doc;
            // Stores the result of ctx.getColor(R.color.np_primary) in color for use by later logic.
            color = ctx.getColor(R.color.np_primary);
        // Closes the current method, control-flow, or type block.
        }
        // Calls b.ivIcon.setImageResource with iconRes to perform the operation shown.
        b.ivIcon.setImageResource(iconRes);
        // Calls b.ivIcon.setImageTintList with ColorStateList.valueOf(color to perform the operation shown.
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        // Calls b.iconTile.setBackgroundTintList with ColorStateList.valueOf(TagStyle.soft(hex(color to perform the operation shown.
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf(TagStyle.soft(hex(color))));

        // Calls b.btnFav.setImageResource with n.favorite ? R.drawable.ic_star : R.drawable.ic_star_border to perform the operation shown.
        b.btnFav.setImageResource(n.favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
        // Calls b.btnFav.setImageTintList with ColorStateList.valueOf(ctx.getColor( to perform the operation shown.
        b.btnFav.setImageTintList(ColorStateList.valueOf(ctx.getColor(
                // Executes n.favorite ? R.color.np_star : R.color.np_text_secondary))) as a statement.
                n.favorite ? R.color.np_star : R.color.np_text_secondary)));
        // Calls b.ivPin.setVisibility with n.pinned ? View.VISIBLE : View.GONE to perform the operation shown.
        b.ivPin.setVisibility(n.pinned ? View.VISIBLE : View.GONE);

        // Calls b.tvDate.setText with DateTimeUtils.formatDateTime(n.updatedAt to perform the operation shown.
        b.tvDate.setText(DateTimeUtils.formatDateTime(n.updatedAt));
        // If compact is true, { immediately.
        if (compact) {
            // Calls b.tvStats.setVisibility with View.GONE to perform the operation shown.
            b.tvStats.setVisibility(View.GONE);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls b.tvStats.setVisibility with View.VISIBLE to perform the operation shown.
            b.tvStats.setVisibility(View.VISIBLE);
            // Calls b.tvStats.setText with ctx.getString(R.string.words_chars_short, n.wordCount, n.charCount to perform the operation shown.
            b.tvStats.setText(ctx.getString(R.string.words_chars_short, n.wordCount, n.charCount));
        // Closes the current method, control-flow, or type block.
        }

        // Calls b.llTags.removeAllViews with  to perform the operation shown.
        b.llTags.removeAllViews();
        // If detailed && !n.tags.isEmpty() is true, { immediately.
        if (detailed && !n.tags.isEmpty()) {
            // Calls b.llTags.setVisibility with View.VISIBLE to perform the operation shown.
            b.llTags.setVisibility(View.VISIBLE);
            // Repeats the following block according to the initialization, condition, and update in Tag t : n.tags.
            for (Tag t : n.tags) {
                // Initializes pill with UiUtils.tagPill(ctx, t) so later statements can use the computed value.
                View pill = UiUtils.tagPill(ctx, t);
                // Initializes lp with new LinearLayout.LayoutParams( so later statements can use the computed value.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        // Executes ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) as a statement.
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Calls lp.setMarginEnd with UiUtils.dp(ctx, 6 to perform the operation shown.
                lp.setMarginEnd(UiUtils.dp(ctx, 6));
                // Calls b.llTags.addView with pill, lp to perform the operation shown.
                b.llTags.addView(pill, lp);
            // Closes the current method, control-flow, or type block.
            }
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls b.llTags.setVisibility with View.GONE to perform the operation shown.
            b.llTags.setVisibility(View.GONE);
        // Closes the current method, control-flow, or type block.
        }

        // Calls b.getRoot with ).setOnClickListener(v -> listener.onOpen(n to perform the operation shown.
        b.getRoot().setOnClickListener(v -> listener.onOpen(n));
        // Calls b.btnFav.setOnClickListener with v -> listener.onFavoriteToggle(n to perform the operation shown.
        b.btnFav.setOnClickListener(v -> listener.onFavoriteToggle(n));
        // Calls b.btnMenu.setOnClickListener with v -> listener.onMenu(n, v to perform the operation shown.
        b.btnMenu.setOnClickListener(v -> listener.onMenu(n, v));
    // Closes the current method, control-flow, or type block.
    }

    // Declares hex() to implement this part of the component.
    private static String hex(int color) {
        // Returns String.format("#%06X", 0xFFFFFF & color) to the caller.
        return String.format("#%06X", 0xFFFFFF & color);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Returns the requested item count.
    public int getItemCount() {
        // Returns items.size() to the caller.
        return items.size();
    // Closes the current method, control-flow, or type block.
    }

    // Declares class Holder to group related state and behavior.
    static class Holder extends RecyclerView.ViewHolder {
        // Declares b for use by the surrounding method or class.
        final ItemNoteBinding b;

        // Calls Holder with ItemNoteBinding b) { to perform the operation shown.
        Holder(ItemNoteBinding b) {
            // Initializes the parent class by passing b.getRoot()) to its constructor.
            super(b.getRoot());
            // Stores the result of b in this.b for use by later logic.
            this.b = b;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
