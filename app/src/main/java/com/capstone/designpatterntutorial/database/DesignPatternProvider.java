package com.capstone.designpatterntutorial.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.designpatterntutorial.database.DesignPatternContract.FavoritePatternEntry;

/**
 * Created by gubbave on 4/17/2017.
 */

public class DesignPatternProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int CATEGORY = 100;
    public static final int PATTERN = 101;
    public static final int FAVORITE_PATTERN = 102;
    public static final int RECENT_PATTERN = 103;

    private DesignPatternDbHelper mOpenHelper;


    @Override
    public boolean onCreate() {
        mOpenHelper = new DesignPatternDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DesignPatternContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PATTERN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DesignPatternContract.PatternEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_PATTERN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DesignPatternContract.FavoritePatternEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECENT_PATTERN:
                cursor = mOpenHelper.getReadableDatabase().query(
                        DesignPatternContract.RecentPatternEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Design Pattern Tutorial.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE_PATTERN:
                long _id = db.insert(FavoritePatternEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = FavoritePatternEntry.buildPatternUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";

        switch (match) {
            case FAVORITE_PATTERN:
                rowsDeleted = db.delete(FavoritePatternEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DesignPatternContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DesignPatternContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, DesignPatternContract.PATH_PATTERN, PATTERN);
        matcher.addURI(authority, DesignPatternContract.PATH_FAVORITE_PATTERN, FAVORITE_PATTERN);
        matcher.addURI(authority, DesignPatternContract.PATH_RECENT_PATTERN, RECENT_PATTERN);

        return matcher;
    }
}
