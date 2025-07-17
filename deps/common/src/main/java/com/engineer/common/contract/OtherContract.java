package com.engineer.common.contract;

import android.net.Uri;
import android.provider.BaseColumns;

public class OtherContract {
    public static final String CONTENT_AUTHORITY = "com.engineer.other.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ENTRY = "entries";

    public static class Entry implements BaseColumns {

        public static final Uri CONTENT_URL = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRY).build();
    }
}
