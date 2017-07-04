package fr.altoine.jnogging.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.R.attr.id;

/**
 * Created by soulierantoine on 04/07/2017.
 */

public class RunContentProvider extends ContentProvider {
    public static final int RUNS = 100;
    public static final int STEPS = 200;
    public static final int RUN_WITH_ID = 101;
    public static final int STEP_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RunContract.AUTHORITY, RunContract.PATH_RUNS, RUNS);
        uriMatcher.addURI(RunContract.AUTHORITY, RunContract.PATH_RUNS + "/#", RUN_WITH_ID);

        uriMatcher.addURI(RunContract.AUTHORITY, RunContract.PATH_STEPS, STEPS);
        uriMatcher.addURI(RunContract.AUTHORITY, RunContract.PATH_STEPS + "/#", STEP_WITH_ID);

        return uriMatcher;
    }

    private RunDbHelper mRunDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRunDbHelper = new RunDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mRunDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case RUNS:
                cursor = db.query(
                        RunContract.RunsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STEPS:
                cursor = db.query(
                        RunContract.StepsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mRunDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case RUNS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RunContract.RunsEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            ++rowsInserted;
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0)
                    getContext().getContentResolver().notifyChange(uri, null);

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mRunDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnedUri;

        switch (match) {
            case RUNS:
                if ((db.insert(RunContract.RunsEntry.TABLE_NAME, null, values)) > 0)
                    returnedUri = ContentUris.withAppendedId(RunContract.RunsEntry.CONTENT_URI, id);
                else
                    throw new SQLException("Failed to insert into row " + uri);
                break;
            case STEPS:

                if ((db.insert(RunContract.StepsEntry.TABLE_NAME, null, values)) > 0)
                    returnedUri = ContentUris.withAppendedId(RunContract.StepsEntry.CONTENT_URI, id);
                else
                    throw new SQLException("Failed to insert into row " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mRunDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int elementDeleted = 0;

        switch (match) {
            case RUN_WITH_ID:
                elementDeleted = db.delete(
                        RunContract.RunsEntry.TABLE_NAME,
                        "_id = ?",
                        new String[] {uri.getLastPathSegment()}
                );
                break;
            case STEP_WITH_ID:
                elementDeleted = db.delete(
                        RunContract.StepsEntry.TABLE_NAME,
                        "_id = ?",
                        new String[] {uri.getLastPathSegment()}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (elementDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return elementDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
