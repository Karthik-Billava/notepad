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
// Imports android.widget.FrameLayout so this file can use that type by its short name.
import android.widget.FrameLayout;
// Imports android.widget.ImageView so this file can use that type by its short name.
import android.widget.ImageView;
// Imports android.widget.TextView so this file can use that type by its short name.
import android.widget.TextView;

// Imports androidx.annotation.NonNull so this file can use that type by its short name.
import androidx.annotation.NonNull;
// Imports androidx.recyclerview.widget.RecyclerView so this file can use that type by its short name.
import androidx.recyclerview.widget.RecyclerView;

// Imports com.google.android.material.card.MaterialCardView so this file can use that type by its short name.
import com.google.android.material.card.MaterialCardView;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ItemTagCardBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ItemTagCardBinding;
// Imports com.mit.notepad.databinding.ItemTagRecentBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ItemTagRecentBinding;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.TagStyle so this file can use that type by its short name.
import com.mit.notepad.utils.TagStyle;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Shows tags either as coloured grid cards or as "recent tag" rows. */
// Declares class TagAdapter to group related state and behavior.
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.Holder> {

    // Declares interface Listener to group related state and behavior.
    public interface Listener {
        // Calls onOpen with Tag tag to perform the operation shown.
        void onOpen(Tag tag);

        // Calls onMenu with Tag tag, View anchor to perform the operation shown.
        void onMenu(Tag tag, View anchor);
    // Closes the current method, control-flow, or type block.
    }

    // Initializes items with new ArrayList<>() so later statements can use the computed value.
    private final List<Tag> items = new ArrayList<>();
    // Declares recentStyle for use by the surrounding method or class.
    private final boolean recentStyle;
    // Declares listener for use by the surrounding method or class.
    private final Listener listener;

    // Declares TagAdapter() to implement this part of the component.
    public TagAdapter(boolean recentStyle, Listener listener) {
        // Stores the result of recentStyle in this.recentStyle for use by later logic.
        this.recentStyle = recentStyle;
        // Stores the result of listener in this.listener for use by later logic.
        this.listener = listener;
    // Closes the current method, control-flow, or type block.
    }

    // Updates items.
    public void setItems(List<Tag> tags) {
        // Calls items.clear with  to perform the operation shown.
        items.clear();
        // Calls items.addAll with tags to perform the operation shown.
        items.addAll(tags);
        // Calls notifyDataSetChanged with  to perform the operation shown.
        notifyDataSetChanged();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @NonNull metadata to the declaration that follows.
    @NonNull
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Inflates and wraps a RecyclerView row layout.
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initializes inflater with LayoutInflater.from(parent.getContext()) so later statements can use the computed value.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // If recentStyle is true, { immediately.
        if (recentStyle) {
            // Initializes b with ItemTagRecentBinding.inflate(inflater, parent, false) so later statements can use the computed value.
            ItemTagRecentBinding b = ItemTagRecentBinding.inflate(inflater, parent, false);
            // Returns new Holder(b.getRoot(), b.iconTile, b.ivIcon, b.tvName, b.tvCount, b.tvUsed, null, null) to the caller.
            return new Holder(b.getRoot(), b.iconTile, b.ivIcon, b.tvName, b.tvCount, b.tvUsed, null, null);
        // Closes the current method, control-flow, or type block.
        }
        // Initializes b with ItemTagCardBinding.inflate(inflater, parent, false) so later statements can use the computed value.
        ItemTagCardBinding b = ItemTagCardBinding.inflate(inflater, parent, false);
        // Returns new Holder(b.getRoot(), b.iconTile, b.ivIcon, b.tvName, b.tvCount, null, b.btnMenu, b.card) to the caller.
        return new Holder(b.getRoot(), b.iconTile, b.ivIcon, b.tvName, b.tvCount, null, b.btnMenu, b.card);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Fills the RecyclerView row with the item at the requested position.
    public void onBindViewHolder(@NonNull Holder h, int position) {
        // Initializes t with items.get(position) so later statements can use the computed value.
        Tag t = items.get(position);
        // Initializes ctx with h.root.getContext() so later statements can use the computed value.
        Context ctx = h.root.getContext();
        // Initializes color with TagStyle.color(t.color) so later statements can use the computed value.
        int color = TagStyle.color(t.color);
        // Calls h.name.setText with t.name to perform the operation shown.
        h.name.setText(t.name);
        // Calls h.count.setText with ctx.getResources().getQuantityString(R.plurals.notes_count, t.noteCount, t.noteCount to perform the operation shown.
        h.count.setText(ctx.getResources().getQuantityString(R.plurals.notes_count, t.noteCount, t.noteCount));
        // Calls h.icon.setImageResource with TagStyle.iconRes(t.icon to perform the operation shown.
        h.icon.setImageResource(TagStyle.iconRes(t.icon));
        // Calls h.icon.setImageTintList with ColorStateList.valueOf(color to perform the operation shown.
        h.icon.setImageTintList(ColorStateList.valueOf(color));
        // Calls h.iconTile.setBackgroundTintList with ColorStateList.valueOf(TagStyle.withAlpha(t.color, 0x33 to perform the operation shown.
        h.iconTile.setBackgroundTintList(ColorStateList.valueOf(TagStyle.withAlpha(t.color, 0x33)));
        // If h.card != null is true, h.card.setCardBackgroundColor(TagStyle.soft(t.color)) immediately.
        if (h.card != null) h.card.setCardBackgroundColor(TagStyle.soft(t.color));
        // If h.used != null is true, { immediately.
        if (h.used != null) {
            // Calls h.used.setText with ctx.getString(R.string.used_ago, DateTimeUtils.relative(t.lastUsedAt to perform the operation shown.
            h.used.setText(ctx.getString(R.string.used_ago, DateTimeUtils.relative(t.lastUsedAt)));
        // Closes the current method, control-flow, or type block.
        }
        // Calls h.root.setOnClickListener with v -> listener.onOpen(t to perform the operation shown.
        h.root.setOnClickListener(v -> listener.onOpen(t));
        // If h.menu != null is true, h.menu.setOnClickListener(v -> listener.onMenu(t, v)) immediately.
        if (h.menu != null) h.menu.setOnClickListener(v -> listener.onMenu(t, v));
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
        // Declares root for use by the surrounding method or class.
        final View root;
        // Declares iconTile for use by the surrounding method or class.
        final FrameLayout iconTile;
        // Declares icon for use by the surrounding method or class.
        final ImageView icon;
        // Declares name for use by the surrounding method or class.
        final TextView name;
        // Declares count for use by the surrounding method or class.
        final TextView count;
        // Declares used for use by the surrounding method or class.
        final TextView used;
        // Declares menu for use by the surrounding method or class.
        final View menu;
        // Declares card for use by the surrounding method or class.
        final MaterialCardView card;

        // Calls Holder with View root, FrameLayout iconTile, ImageView icon, TextView name, TextView count, to perform the operation shown.
        Holder(View root, FrameLayout iconTile, ImageView icon, TextView name, TextView count,
               // Declares menu for use by the surrounding method or class.
               TextView used, View menu, MaterialCardView card) {
            // Initializes the parent class by passing root) to its constructor.
            super(root);
            // Stores the result of root in this.root for use by later logic.
            this.root = root;
            // Stores the result of iconTile in this.iconTile for use by later logic.
            this.iconTile = iconTile;
            // Stores the result of icon in this.icon for use by later logic.
            this.icon = icon;
            // Stores the result of name in this.name for use by later logic.
            this.name = name;
            // Stores the result of count in this.count for use by later logic.
            this.count = count;
            // Stores the result of used in this.used for use by later logic.
            this.used = used;
            // Stores the result of menu in this.menu for use by later logic.
            this.menu = menu;
            // Stores the result of card in this.card for use by later logic.
            this.card = card;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
