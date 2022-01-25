package com.engineer.android.mini.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeUtil {

    /**
     * @param timestamp 1643099624583
     * @return 2022年1月25日 下午4:33:44
     * <p>
     * sysTime = 1643099624583, date = 2022年1月25日 下午4:33:44
     */
    public static String getTime(long timestamp) {
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
        return formatter.format(timestamp);
    }
}
