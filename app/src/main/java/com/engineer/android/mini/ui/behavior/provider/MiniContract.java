package com.engineer.android.mini.ui.behavior.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MiniContract {
    public static final String CONTENT_AUTHORITY = "mini.provider.info.adapter";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ENTRY = "entries";

    public static class Entry implements BaseColumns {
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.basicsyncadapter.entries";

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.basicsyncadapter.entry";

        public static final Uri CONTENT_URL = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRY).build();

        public static final String TABLE_NAME = "mini_table";
        public static final String COLUMN_KEY = "column_key";
        public static final String COLUMN_VALUE = "column_value";
    }
}
