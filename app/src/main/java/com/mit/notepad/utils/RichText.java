package com.mit.notepad.utils;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Serialises text formatting (bold, italic, underline, links) to a small JSON array so the
 * plain text stays searchable while the formatting is preserved.
 * Each entry: {"t":"b|i|u|l","s":start,"e":end,"u":url}
 */
public final class RichText {
    private RichText() { }

    public static String toJson(Spanned text) {
        JSONArray array = new JSONArray();
        try {
            for (Object span : text.getSpans(0, text.length(), Object.class)) {
                if ((text.getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;
                String type = null;
                String url = null;
                if (span instanceof StyleSpan) {
                    int style = ((StyleSpan) span).getStyle();
                    if (style == Typeface.BOLD) type = "b";
                    else if (style == Typeface.ITALIC) type = "i";
                } else if (span instanceof URLSpan) {
                    type = "l";
                    url = ((URLSpan) span).getURL();
                } else if (span instanceof UnderlineSpan) {
                    type = "u";
                }
                if (type == null) continue;
                JSONObject o = new JSONObject();
                o.put("t", type);
                o.put("s", text.getSpanStart(span));
                o.put("e", text.getSpanEnd(span));
                if (url != null) o.put("u", url);
                array.put(o);
            }
        } catch (JSONException e) {
            return "[]";
        }
        return array.toString();
    }

    public static Spannable fromJson(String plain, String json) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(plain == null ? "" : plain);
        if (json == null || json.isEmpty()) return ssb;
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                int s = Math.max(0, o.getInt("s"));
                int e = Math.min(ssb.length(), o.getInt("e"));
                if (s >= e) continue;
                String t = o.getString("t");
                Object span;
                if ("b".equals(t)) span = new StyleSpan(Typeface.BOLD);
                else if ("i".equals(t)) span = new StyleSpan(Typeface.ITALIC);
                else if ("u".equals(t)) span = new UnderlineSpan();
                else if ("l".equals(t)) span = new URLSpan(o.optString("u", ""));
                else continue;
                ssb.setSpan(span, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (JSONException ignored) {
            // Corrupt formatting is ignored; the plain text is never lost.
        }
        return ssb;
    }
}
