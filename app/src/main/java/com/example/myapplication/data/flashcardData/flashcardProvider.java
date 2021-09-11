package com.example.myapplication.data.flashcardData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;


public class flashcardProvider extends ContentProvider {
    private flashcardDbHelper mDbHelper;

    private static final int flashcards = 100;
    private static final int flashcard_id = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(flashcardContract.CONTENT_AUTHORITY, flashcardContract.PATH_FLASHCARD_SETS, flashcards);
        sUriMatcher.addURI(flashcardContract.CONTENT_AUTHORITY, flashcardContract.PATH_FLASHCARD_SETS + "/#", flashcard_id);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new flashcardDbHelper(getContext());
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
            case flashcards:
                cursor = database.query(flashcardEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case flashcard_id:
                selection = flashcardEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(flashcardEntry.TABLE_NAME, projection, selection, selectionArgs,
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
            case flashcards:
                return flashcardEntry.CONTENT_LIST_TYPE;
            case flashcard_id:
                return flashcardEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcards:
                return insertFlashcard(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertFlashcard(Uri uri, ContentValues values){
        String term = values.getAsString(flashcardEntry.COLUMN_FLASHCARD_TERM);
        if (term == null){
            throw new IllegalArgumentException("Flashcard requires a term");
        }
        String definition = values.getAsString(flashcardEntry.COLUMN_FLASHCARD_DEFINITION);
        if (definition == null){
            throw new IllegalArgumentException("Flashcard requires a definition");
        }
        int flashcard_set_id = values.getAsInteger(flashcardEntry.FLASHCARD_SET_ID);
        if (flashcard_set_id == 0){
            throw new IllegalArgumentException("Flashcard requires a flashcard set id");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(flashcardEntry.TABLE_NAME, null, values);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case flashcards:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(flashcardEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case flashcard_id:
                // Delete a single row given by the ID in the URI
                selection = flashcardEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(flashcardEntry.TABLE_NAME, selection, selectionArgs);
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
            case flashcards:
                return updateFlashcard(uri, values, selection, selectionArgs);
            case flashcard_id:
                selection = flashcardEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateFlashcard(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateFlashcard(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey(flashcardEntry.COLUMN_FLASHCARD_TERM)){
            String term = values.getAsString(flashcardEntry.COLUMN_FLASHCARD_TERM);
            if (term == null){
                throw new IllegalArgumentException("Flashcard requires a term");
            }
        }
        if (values.containsKey(flashcardEntry.COLUMN_FLASHCARD_DEFINITION)){
            String definition = values.getAsString(flashcardEntry.COLUMN_FLASHCARD_DEFINITION);
            if (definition == null){
                throw new IllegalArgumentException("Flashcard requires a definition");
            }
        }
        if (values.containsKey(flashcardEntry.FLASHCARD_SET_ID)){
            int flashcardSetId = values.getAsInteger(flashcardEntry.FLASHCARD_SET_ID);
            if (flashcardSetId == 0){
                throw new IllegalArgumentException("Flashcard requires a flashcard set id");
            }
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numberOfRows = db.update(flashcardEntry.TABLE_NAME, values, selection, selectionArgs);
        return numberOfRows;

    }
}
