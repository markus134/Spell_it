package com.example.myapplication.data.flashcardSetData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.data.flashcardSetData.flashcardSetContract.flashcardSetEntry;

public class flashcardSetProvider extends ContentProvider {
    private flashcardSetDbHelper mDbHelper;

    private static final int flashcardSets = 100;
    private static final int flashcardSet_id = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(flashcardSetContract.CONTENT_AUTHORITY, flashcardSetContract.PATH_FLASHCARD_SETS, flashcardSets);
        sUriMatcher.addURI(flashcardSetContract.CONTENT_AUTHORITY, flashcardSetContract.PATH_FLASHCARD_SETS + "/#", flashcardSet_id);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new flashcardSetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcardSets:
                cursor = database.query(flashcardSetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case flashcardSet_id:
                selection = flashcardSetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(flashcardSetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcardSets:
                return flashcardSetEntry.CONTENT_LIST_TYPE;
            case flashcardSet_id:
                return flashcardSetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcardSets:
                return insertFlashcardSet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertFlashcardSet(Uri uri, ContentValues values){
        String name = values.getAsString(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME);
        if (name == null){
            throw new IllegalArgumentException("Flashcard set requires a name");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(flashcardSetEntry.TABLE_NAME, null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcardSets:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(flashcardSetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case flashcardSet_id:
                // Delete a single row given by the ID in the URI
                selection = flashcardSetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(flashcardSetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcardSets:
                return updateFlashcardSet(uri, values, selection, selectionArgs);
            case flashcardSet_id:
                selection = flashcardSetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateFlashcardSet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateFlashcardSet(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME)){
            String name = values.getAsString(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME);
            if (name == null){
                throw new IllegalArgumentException("Flashcard set requires a name");
            }
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberOfRows = db.update(flashcardSetEntry.TABLE_NAME, values, selection, selectionArgs);
        return numberOfRows;

    }
}
