// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.graphics.drawable.GradientDrawable so this file can use that type by its short name.
import android.graphics.drawable.GradientDrawable;
// Imports android.view.LayoutInflater so this file can use that type by its short name.
import android.view.LayoutInflater;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.widget.LinearLayout so this file can use that type by its short name.
import android.widget.LinearLayout;

// Imports androidx.appcompat.app.AlertDialog so this file can use that type by its short name.
import androidx.appcompat.app.AlertDialog;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.DialogTagEditBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.DialogTagEditBinding;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.repositories.TagRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TagRepository;
// Imports com.mit.notepad.utils.TagStyle so this file can use that type by its short name.
import com.mit.notepad.utils.TagStyle;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;
// Imports com.mit.notepad.utils.Validator so this file can use that type by its short name.
import com.mit.notepad.utils.Validator;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.LinkedHashSet so this file can use that type by its short name.
import java.util.LinkedHashSet;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Set so this file can use that type by its short name.
import java.util.Set;

/** Tag selection and create/rename dialogs shared by several screens. */
// Declares class TagDialogs to group related state and behavior.
public final class TagDialogs {
    // Declares TagDialogs() to implement this part of the component.
    private TagDialogs() { }

    // Declares interface TagsResult to group related state and behavior.
    public interface TagsResult {
        // Calls onTags with List<Long> tagIds to perform the operation shown.
        void onTags(List<Long> tagIds);
    // Closes the current method, control-flow, or type block.
    }

    /** Multi-select tag picker; the neutral button creates a new tag and re-opens the picker. */
    // Prompts the user to choose tags.
    public static void pickTags(Context ctx, List<Long> selected, TagsResult callback) {
        // Initializes tags with new TagRepository(ctx).getAll() so later statements can use the computed value.
        List<Tag> tags = new TagRepository(ctx).getAll();
        // Initializes names with new String[tags.size()] so later statements can use the computed value.
        String[] names = new String[tags.size()];
        // Calls tags.size with )] to perform the operation shown.
        boolean[] checked = new boolean[tags.size()];
        // Initializes chosen with new LinkedHashSet<>(selected) so later statements can use the computed value.
        Set<Long> chosen = new LinkedHashSet<>(selected);
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < tags.size(); i++.
        for (int i = 0; i < tags.size(); i++) {
            // Stores the result of tags.get(i).name in names[i] for use by later logic.
            names[i] = tags.get(i).name;
            // Stores the result of chosen.contains(tags.get(i).id) in checked[i] for use by later logic.
            checked[i] = chosen.contains(tags.get(i).id);
        // Closes the current method, control-flow, or type block.
        }
        // Creates the object described by new MaterialAlertDialogBuilder(ctx).
        new MaterialAlertDialogBuilder(ctx)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.select_tags)
                // Starts the callback body that handles the event or asynchronous result.
                .setMultiChoiceItems(names, checked, (d, which, isChecked) -> {
                    // Initializes id with tags.get(which).id so later statements can use the computed value.
                    long id = tags.get(which).id;
                    // If isChecked is true, chosen.add(id); else chosen.remove(id) immediately.
                    if (isChecked) chosen.add(id); else chosen.remove(id);
                // Continues the expression above with }).
                })
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.done, (d, w) -> callback.onTags(new ArrayList<>(chosen)))
                // Calls setNeutralButton on the object from the preceding line with the shown arguments.
                .setNeutralButton(R.string.new_tag, (d, w) ->
                        // Calls editTag with ctx, null, () -> pickTags(ctx, new ArrayList<>(chosen), callback to perform the operation shown.
                        editTag(ctx, null, () -> pickTags(ctx, new ArrayList<>(chosen), callback)))
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    /** Create (existing == null) or rename/recolour a tag. */
    // Declares editTag() to implement this part of the component.
    public static void editTag(Context ctx, Tag existing, Runnable onDone) {
        // Initializes b with DialogTagEditBinding.inflate(LayoutInflater.from(ctx)) so later statements can use the computed value.
        DialogTagEditBinding b = DialogTagEditBinding.inflate(LayoutInflater.from(ctx));
        // Initializes color with {existing != null ? existing.color : TagStyle.PALETTE[0]} so later statements can use the computed value.
        String[] color = {existing != null ? existing.color : TagStyle.PALETTE[0]};
        // If existing != null is true, b.etTagName.setText(existing.name) immediately.
        if (existing != null) b.etTagName.setText(existing.name);
        // Calls buildSwatches with ctx, b.colorRow, color to perform the operation shown.
        buildSwatches(ctx, b.colorRow, color);

        // Initializes dialog with new MaterialAlertDialogBuilder(ctx) so later statements can use the computed value.
        AlertDialog dialog = new MaterialAlertDialogBuilder(ctx)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(existing == null ? R.string.new_tag : R.string.rename_edit)
                // Calls setView on the object from the preceding line with the shown arguments.
                .setView(b.getRoot())
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.save, null)
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls create on the object from the preceding line with the shown arguments.
                .create();
        // Starts the callback body that handles the event or asynchronous result.
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Initializes name with b.etTagName.getText().toString().trim() so later statements can use the computed value.
            String name = b.etTagName.getText().toString().trim();
            // Initializes error with Validator.tagNameError(name) so later statements can use the computed value.
            int error = Validator.tagNameError(name);
            // If error != 0 is true, { immediately.
            if (error != 0) {
                // Calls b.etTagName.setError with ctx.getString(error to perform the operation shown.
                b.etTagName.setError(ctx.getString(error));
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
            // Starts the operation whose failures are handled by the following catch clauses.
            try {
                // Initializes repo with new TagRepository(ctx) so later statements can use the computed value.
                TagRepository repo = new TagRepository(ctx);
                // If existing == null is true, repo.create(name, color[0], "label") immediately.
                if (existing == null) repo.create(name, color[0], "label");
                // Runs the fallback branch when the preceding condition did not match.
                else repo.update(existing.id, name, color[0]);
            // Calls catch with TagRepository.DuplicateTagException e) { to perform the operation shown.
            } catch (TagRepository.DuplicateTagException e) {
                // Calls b.etTagName.setError with ctx.getString(R.string.error_tag_exists to perform the operation shown.
                b.etTagName.setError(ctx.getString(R.string.error_tag_exists));
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
            // Calls dialog.dismiss with  to perform the operation shown.
            dialog.dismiss();
            // If onDone != null is true, onDone.run() immediately.
            if (onDone != null) onDone.run();
        // Executes })) as a statement.
        }));
        // Calls dialog.show with  to perform the operation shown.
        dialog.show();
    // Closes the current method, control-flow, or type block.
    }

    // Builds swatches.
    private static void buildSwatches(Context ctx, LinearLayout row, String[] selected) {
        // Calls row.removeAllViews with  to perform the operation shown.
        row.removeAllViews();
        // Initializes size with UiUtils.dp(ctx, 36) so later statements can use the computed value.
        int size = UiUtils.dp(ctx, 36);
        // Repeats the following block according to the initialization, condition, and update in String hex : TagStyle.PALETTE.
        for (String hex : TagStyle.PALETTE) {
            // Initializes swatch with new View(ctx) so later statements can use the computed value.
            View swatch = new View(ctx);
            // Initializes bg with new GradientDrawable() so later statements can use the computed value.
            GradientDrawable bg = new GradientDrawable();
            // Calls bg.setShape with GradientDrawable.OVAL to perform the operation shown.
            bg.setShape(GradientDrawable.OVAL);
            // Calls bg.setColor with TagStyle.color(hex to perform the operation shown.
            bg.setColor(TagStyle.color(hex));
            // If hex.equalsIgnoreCase(selected[0]) is true, { immediately.
            if (hex.equalsIgnoreCase(selected[0])) {
                // Calls bg.setStroke with UiUtils.dp(ctx, 3), ctx.getColor(R.color.np_text_primary to perform the operation shown.
                bg.setStroke(UiUtils.dp(ctx, 3), ctx.getColor(R.color.np_text_primary));
            // Closes the current method, control-flow, or type block.
            }
            // Calls swatch.setBackground with bg to perform the operation shown.
            swatch.setBackground(bg);
            // Calls swatch.setContentDescription with hex to perform the operation shown.
            swatch.setContentDescription(hex);
            // Initializes lp with new LinearLayout.LayoutParams(size, size) so later statements can use the computed value.
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            // Calls lp.setMarginEnd with UiUtils.dp(ctx, 10 to perform the operation shown.
            lp.setMarginEnd(UiUtils.dp(ctx, 10));
            // Starts the callback body that handles the event or asynchronous result.
            swatch.setOnClickListener(v -> {
                // Stores the result of hex in selected[0] for use by later logic.
                selected[0] = hex;
                // Calls buildSwatches with ctx, row, selected to perform the operation shown.
                buildSwatches(ctx, row, selected);
            // Executes }) as a statement.
            });
            // Calls row.addView with swatch, lp to perform the operation shown.
            row.addView(swatch, lp);
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares namesFor() to implement this part of the component.
    public static String namesFor(Context ctx, List<Long> ids) {
        // If ids.isEmpty() is true, return ctx.getString(R.string.no_tag_selected) immediately.
        if (ids.isEmpty()) return ctx.getString(R.string.no_tag_selected);
        // Initializes sb with new StringBuilder() so later statements can use the computed value.
        StringBuilder sb = new StringBuilder();
        // Repeats the following block according to the initialization, condition, and update in Tag t : new TagRepository(ctx).getAll().
        for (Tag t : new TagRepository(ctx).getAll()) {
            // If ids.contains(t.id) is true, { immediately.
            if (ids.contains(t.id)) {
                // If sb.length() > 0 is true, sb.append(", ") immediately.
                if (sb.length() > 0) sb.append(", ");
                // Calls sb.append with t.name to perform the operation shown.
                sb.append(t.name);
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
        // Returns sb.length() == 0 ? ctx.getString(R.string.no_tag_selected) : sb.toString() to the caller.
        return sb.length() == 0 ? ctx.getString(R.string.no_tag_selected) : sb.toString();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
