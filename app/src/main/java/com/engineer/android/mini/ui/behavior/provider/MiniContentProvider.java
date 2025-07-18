package com.engineer.android.mini.ui.behavior.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.engineer.common.contract.ContentProviderHelper;
import com.engineer.common.contract.MiniContract;

public class MiniContentProvider extends ContentProvider {
    private static final String TAG = "MiniContentProvider";
    public static final int ROUTE_ENTRIES = 1;
    public static final int ROUTE_ENTRIES_ID = 2;

    private static final String AUTHORITY = MiniContract.CONTENT_AUTHORITY;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        URI_MATCHER.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);
    }

    public MiniContentProvider() {
    }

    MiniDataBase miniDataBase;

    @Override
    public boolean onCreate() {
        miniDataBase = new MiniDataBase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        return switch (match) {
            case ROUTE_ENTRIES -> MiniContract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID -> MiniContract.Entry.CONTENT_ITEM_TYPE;
            default -> null;
        };
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = miniDataBase.getReadableDatabase();
        int match = URI_MATCHER.match(uri);
        if (match == ROUTE_ENTRIES) {
            return db.query(MiniContract.Entry.TABLE_NAME, projection, selection, selectionArgs, sortOrder, null, null);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = miniDataBase.getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        int count = -1;
        if (match == ROUTE_ENTRIES) {
            count = db.update(MiniContract.Entry.TABLE_NAME, values, selection, selectionArgs);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = miniDataBase.getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        int count = -1;
        if (match == ROUTE_ENTRIES) {
            count = db.delete(MiniContract.Entry.TABLE_NAME, selection, selectionArgs);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = miniDataBase.getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        Uri result = null;
        if (match == ROUTE_ENTRIES) {
            long id = db.insertOrThrow(MiniContract.Entry.TABLE_NAME, null, values);
            result = ContentUris.withAppendedId(MiniContract.Entry.CONTENT_URL, id);
        }
        if (result != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Log.d(TAG, "call() called with: method = [" + method + "], arg = [" + arg + "], thread = [" + Thread.currentThread().getName() + "]");
        handleOtherProcessCall(method, arg, extras);
        return super.call(method, arg, extras);
    }


    private void handleOtherProcessCall(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        switch (method) {
            case "add":
                int a = extras.getInt("a");
                int b = extras.getInt("b");
                int sum = a + b;
                Bundle bundle = new Bundle();
                bundle.putInt("sum", sum);
                ContentProviderHelper.callOtherAppMethod(getContext(), "sum", "", bundle);
                break;
            default:
                break;
        }
    }
}