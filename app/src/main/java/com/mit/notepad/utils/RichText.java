// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports android.graphics.Typeface so this file can use that type by its short name.
import android.graphics.Typeface;
// Imports android.text.Spannable so this file can use that type by its short name.
import android.text.Spannable;
// Imports android.text.SpannableStringBuilder so this file can use that type by its short name.
import android.text.SpannableStringBuilder;
// Imports android.text.Spanned so this file can use that type by its short name.
import android.text.Spanned;
// Imports android.text.style.StyleSpan so this file can use that type by its short name.
import android.text.style.StyleSpan;
// Imports android.text.style.URLSpan so this file can use that type by its short name.
import android.text.style.URLSpan;
// Imports android.text.style.UnderlineSpan so this file can use that type by its short name.
import android.text.style.UnderlineSpan;

// Imports org.json.JSONArray so this file can use that type by its short name.
import org.json.JSONArray;
// Imports org.json.JSONException so this file can use that type by its short name.
import org.json.JSONException;
// Imports org.json.JSONObject so this file can use that type by its short name.
import org.json.JSONObject;

/**
 * Serialises text formatting (bold, italic, underline, links) to a small JSON array so the
 * plain text stays searchable while the formatting is preserved.
 * Each entry: {"t":"b|i|u|l","s":start,"e":end,"u":url}
 */
// Declares class RichText to group related state and behavior.
public final class RichText {
    // Declares RichText() to implement this part of the component.
    private RichText() { }

    // Declares toJson() to implement this part of the component.
    public static String toJson(Spanned text) {
        // Initializes array with new JSONArray() so later statements can use the computed value.
        JSONArray array = new JSONArray();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Repeats the following block according to the initialization, condition, and update in Object span : text.getSpans(0, text.length(), Object.class).
            for (Object span : text.getSpans(0, text.length(), Object.class)) {
                // If (text.getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0 is true, continue immediately.
                if ((text.getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;
                // Initializes type with null so later statements can use the computed value.
                String type = null;
                // Initializes url with null so later statements can use the computed value.
                String url = null;
                // If span instanceof StyleSpan is true, { immediately.
                if (span instanceof StyleSpan) {
                    // Initializes style with ((StyleSpan) span).getStyle() so later statements can use the computed value.
                    int style = ((StyleSpan) span).getStyle();
                    // If style == Typeface.BOLD is true, type = "b" immediately.
                    if (style == Typeface.BOLD) type = "b";
                    // Checks the alternate condition (style == Typeface.ITALIC) after the earlier branch did not run.
                    else if (style == Typeface.ITALIC) type = "i";
                // Checks the alternate condition (span instanceof URLSpan) after the earlier branch did not run.
                } else if (span instanceof URLSpan) {
                    // Stores the result of "l" in type for use by later logic.
                    type = "l";
                    // Stores the result of ((URLSpan) span).getURL() in url for use by later logic.
                    url = ((URLSpan) span).getURL();
                // Checks the alternate condition (span instanceof UnderlineSpan) after the earlier branch did not run.
                } else if (span instanceof UnderlineSpan) {
                    // Stores the result of "u" in type for use by later logic.
                    type = "u";
                // Closes the current method, control-flow, or type block.
                }
                // If type == null is true, continue immediately.
                if (type == null) continue;
                // Initializes o with new JSONObject() so later statements can use the computed value.
                JSONObject o = new JSONObject();
                // Calls o.put with "t", type to perform the operation shown.
                o.put("t", type);
                // Calls o.put with "s", text.getSpanStart(span to perform the operation shown.
                o.put("s", text.getSpanStart(span));
                // Calls o.put with "e", text.getSpanEnd(span to perform the operation shown.
                o.put("e", text.getSpanEnd(span));
                // If url != null is true, o.put("u", url) immediately.
                if (url != null) o.put("u", url);
                // Calls array.put with o to perform the operation shown.
                array.put(o);
            // Closes the current method, control-flow, or type block.
            }
        // Calls catch with JSONException e) { to perform the operation shown.
        } catch (JSONException e) {
            // Returns "[]" to the caller.
            return "[]";
        // Closes the current method, control-flow, or type block.
        }
        // Returns array.toString() to the caller.
        return array.toString();
    // Closes the current method, control-flow, or type block.
    }

    // Declares fromJson() to implement this part of the component.
    public static Spannable fromJson(String plain, String json) {
        // Initializes ssb with the text or query fragment shown here.
        SpannableStringBuilder ssb = new SpannableStringBuilder(plain == null ? "" : plain);
        // If json == null || json.isEmpty() is true, return ssb immediately.
        if (json == null || json.isEmpty()) return ssb;
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Initializes array with new JSONArray(json) so later statements can use the computed value.
            JSONArray array = new JSONArray(json);
            // Repeats the following block according to the initialization, condition, and update in int i = 0; i < array.length(); i++.
            for (int i = 0; i < array.length(); i++) {
                // Initializes o with array.getJSONObject(i) so later statements can use the computed value.
                JSONObject o = array.getJSONObject(i);
                // Initializes s with the text or query fragment shown here.
                int s = Math.max(0, o.getInt("s"));
                // Initializes e with the text or query fragment shown here.
                int e = Math.min(ssb.length(), o.getInt("e"));
                // If s >= e is true, continue immediately.
                if (s >= e) continue;
                // Initializes t with the text or query fragment shown here.
                String t = o.getString("t");
                // Declares span for use by the surrounding method or class.
                Object span;
                // If "b".equals(t) is true, span = new StyleSpan(Typeface.BOLD) immediately.
                if ("b".equals(t)) span = new StyleSpan(Typeface.BOLD);
                // Checks the alternate condition ("i".equals(t) after the earlier branch did not run.
                else if ("i".equals(t)) span = new StyleSpan(Typeface.ITALIC);
                // Checks the alternate condition ("u".equals(t) after the earlier branch did not run.
                else if ("u".equals(t)) span = new UnderlineSpan();
                // Checks the alternate condition ("l".equals(t) after the earlier branch did not run.
                else if ("l".equals(t)) span = new URLSpan(o.optString("u", ""));
                // Runs the fallback branch when the preceding condition did not match.
                else continue;
                // Calls ssb.setSpan with span, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE to perform the operation shown.
                ssb.setSpan(span, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Closes the current method, control-flow, or type block.
            }
        // Calls catch with JSONException ignored) { to perform the operation shown.
        } catch (JSONException ignored) {
            // Corrupt formatting is ignored; the plain text is never lost.
        // Closes the current method, control-flow, or type block.
        }
        // Returns ssb to the caller.
        return ssb;
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
