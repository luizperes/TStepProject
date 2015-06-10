package org.telegram.android;

/* Part of this source code belongs to Telegram for Android v. 1.4.x., Copyright Nikolai Kudashov, 2013.
 * and this source code itself belongs to Stepss for Android v. 1.0
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Luiz Peres, 2015.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;

import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.TypefaceSpan;

import java.util.ArrayList;
import java.util.Hashtable;

public class AndroidUtilities
{
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();

    public static int dp(DisplayMetrics theMetrics, float value)
    {
        return (int)Math.ceil(theMetrics.density * value);
    }

    public static Spannable replaceTags(Context appCtx, AssetManager appAssets, String str)
    {
        try {
            int start = -1;
            int startColor = -1;
            int end = -1;
            StringBuilder stringBuilder = new StringBuilder(str);
            while ((start = stringBuilder.indexOf("<br>")) != -1) {
                stringBuilder.replace(start, start + 4, "\n");
            }
            while ((start = stringBuilder.indexOf("<br/>")) != -1) {
                stringBuilder.replace(start, start + 5, "\n");
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();
            while ((start = stringBuilder.indexOf("<b>")) != -1 || (startColor = stringBuilder.indexOf("<c")) != -1) {
                if (start != -1) {
                    stringBuilder.replace(start, start + 3, "");
                    end = stringBuilder.indexOf("</b>");
                    stringBuilder.replace(end, end + 4, "");
                    bolds.add(start);
                    bolds.add(end);
                } else if (startColor != -1) {
                    stringBuilder.replace(startColor, startColor + 2, "");
                    end = stringBuilder.indexOf(">", startColor);
                    int color = Color.parseColor(stringBuilder.substring(startColor, end));
                    stringBuilder.replace(startColor, end + 1, "");
                    end = stringBuilder.indexOf("</c>");
                    stringBuilder.replace(end, end + 4, "");
                    colors.add(startColor);
                    colors.add(end);
                    colors.add(color);
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(appCtx, appAssets, "fonts/rmedium.ttf")), bolds.get(a * 2), bolds.get(a * 2 + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            for (int a = 0; a < colors.size() / 3; a++) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(colors.get(a * 3 + 2)), colors.get(a * 3), colors.get(a * 3 + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e(appCtx, "tmessages", e);
        }

        return new SpannableStringBuilder(str);
    }

    public static Typeface getTypeface(Context appCtx, AssetManager appAssets, String assetPath) {
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(appAssets, assetPath);
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    FileLog.e(appCtx, "Typefaces", "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    return null;
                }
            }

            return typefaceCache.get(assetPath);
        }
    }


}
