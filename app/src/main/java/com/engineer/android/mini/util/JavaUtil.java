package com.engineer.android.mini.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import com.engineer.android.mini.ext.NumExtKt;

public class JavaUtil {
    public static SpannableString getSpannableString(float length, String description) {
        SpannableString spannableString = new SpannableString(description);
        int marginSpanSize = (int) length;
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(marginSpanSize, 0);//仅首行缩进
        spannableString.setSpan(leadingMarginSpan, 0, description.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
