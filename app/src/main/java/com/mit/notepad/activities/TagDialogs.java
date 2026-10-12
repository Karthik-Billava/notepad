package com.mit.notepad.activities;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.databinding.DialogTagEditBinding;
import com.mit.notepad.models.Tag;
import com.mit.notepad.repositories.TagRepository;
import com.mit.notepad.utils.TagStyle;
import com.mit.notepad.utils.UiUtils;
import com.mit.notepad.utils.Validator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Tag selection and create/rename dialogs shared by several screens. */
public final class TagDialogs {
    private TagDialogs() { }

    public interface TagsResult {
        void onTags(List<Long> tagIds);
    }

    /** Multi-select tag picker; the neutral button creates a new tag and re-opens the picker. */
    public static void pickTags(Context ctx, List<Long> selected, TagsResult callback) {
        List<Tag> tags = new TagRepository(ctx).getAll();
        String[] names = new String[tags.size()];
        boolean[] checked = new boolean[tags.size()];
        Set<Long> chosen = new LinkedHashSet<>(selected);
        for (int i = 0; i < tags.size(); i++) {
            names[i] = tags.get(i).name;
            checked[i] = chosen.contains(tags.get(i).id);
        }
        new MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.select_tags)
                .setMultiChoiceItems(names, checked, (d, which, isChecked) -> {
                    long id = tags.get(which).id;
                    if (isChecked) chosen.add(id); else chosen.remove(id);
                })
                .setPositiveButton(R.string.done, (d, w) -> callback.onTags(new ArrayList<>(chosen)))
                .setNeutralButton(R.string.new_tag, (d, w) ->
                        editTag(ctx, null, () -> pickTags(ctx, new ArrayList<>(chosen), callback)))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /** Create (existing == null) or rename/recolour a tag. */
    public static void editTag(Context ctx, Tag existing, Runnable onDone) {
        DialogTagEditBinding b = DialogTagEditBinding.inflate(LayoutInflater.from(ctx));
        String[] color = {existing != null ? existing.color : TagStyle.PALETTE[0]};
        if (existing != null) b.etTagName.setText(existing.name);
        buildSwatches(ctx, b.colorRow, color);

        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                .setTitle(existing == null ? R.string.new_tag : R.string.rename_edit)
                .setView(b.getRoot())
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = b.etTagName.getText().toString().trim();
            int error = Validator.tagNameError(name);
            if (error != 0) {
                b.etTagName.setError(ctx.getString(error));
                return;
            }
            try {
                TagRepository repo = new TagRepository(ctx);
                if (existing == null) repo.create(name, color[0], "label");
                else repo.update(existing.id, name, color[0]);
            } catch (TagRepository.DuplicateTagException e) {
                b.etTagName.setError(ctx.getString(R.string.error_tag_exists));
                return;
            }
            dialog.dismiss();
            if (onDone != null) onDone.run();
        }));
        dialog.show();
    }

    private static void buildSwatches(Context ctx, LinearLayout row, String[] selected) {
        row.removeAllViews();
        int size = UiUtils.dp(ctx, 36);
        for (String hex : TagStyle.PALETTE) {
            View swatch = new View(ctx);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(TagStyle.color(hex));
            if (hex.equalsIgnoreCase(selected[0])) {
                bg.setStroke(UiUtils.dp(ctx, 3), ctx.getColor(R.color.np_text_primary));
            }
            swatch.setBackground(bg);
            swatch.setContentDescription(hex);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMarginEnd(UiUtils.dp(ctx, 10));
            swatch.setOnClickListener(v -> {
                selected[0] = hex;
                buildSwatches(ctx, row, selected);
            });
            row.addView(swatch, lp);
        }
    }

    public static String namesFor(Context ctx, List<Long> ids) {
        if (ids.isEmpty()) return ctx.getString(R.string.no_tag_selected);
        StringBuilder sb = new StringBuilder();
        for (Tag t : new TagRepository(ctx).getAll()) {
            if (ids.contains(t.id)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(t.name);
            }
        }
        return sb.length() == 0 ? ctx.getString(R.string.no_tag_selected) : sb.toString();
    }
}
