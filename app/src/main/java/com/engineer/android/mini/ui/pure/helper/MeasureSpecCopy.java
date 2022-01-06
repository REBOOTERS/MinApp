package com.engineer.android.mini.ui.pure.helper;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MeasureSpecCopy {
    private static final int MODE_SHIFT = 30;
    private static final int MODE_MASK = 0x3 << MODE_SHIFT;


    @IntDef({UNSPECIFIED, EXACTLY, AT_MOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MeasureSpecMode {
    }

    public static final int UNSPECIFIED = 0 << MODE_SHIFT;


    public static final int EXACTLY = 1 << MODE_SHIFT;


    public static final int AT_MOST = 2 << MODE_SHIFT;

    public static void print() {

        System.out.println("MODE_MASK==" + Integer.toBinaryString(MODE_MASK));
        System.out.println("EXACTLY  ==" + Integer.toBinaryString(EXACTLY));
        System.out.println("AT_MOST  ==" + Integer.toBinaryString(AT_MOST));


        System.out.println("MODE_MASK & EXACTLY = " + (EXACTLY & MODE_MASK));
        System.out.println("MODE_MASK & AT_MOST = " + (AT_MOST & MODE_MASK));
    }
}
