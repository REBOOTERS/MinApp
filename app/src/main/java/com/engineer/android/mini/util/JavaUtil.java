package com.engineer.android.mini.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

/**
 * r
 */
public class JavaUtil {
    /**
     * set span for text,
     *
     * @param length      length
     * @param description desc
     * @return result
     *
     */
    public static SpannableString getSpannableString(float length, String description) {
        SpannableString spannableString = new SpannableString(description);
        int marginSpanSize = (int) length;
        LeadingMarginSpan leadingMarginSpan =
                new LeadingMarginSpan.Standard(marginSpanSize, 0); // 仅首行缩进
        spannableString.setSpan(leadingMarginSpan,
                0, description.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * @param input input
     * @param <T>   t
     * @return result
     */
    public <T extends String> String test(T input) {
        String aaa = "abcdefg";
        String sub = "ace";
        int a = aaa.indexOf(sub);
        T subStr = (T) input.substring(3);
        return subStr;
    }
}
