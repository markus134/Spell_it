package com.example.myapplication.data.flashcardData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;

public class flashcardDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "flashcards.db";
    public static final int DATABASE_VERSION = 1;

    public flashcardDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + flashcardEntry.TABLE_NAME + " (" + flashcardEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " + flashcardEntry.FLASHCARD_SET_ID + " INTEGER" + ", " + flashcardEntry.COLUMN_FLASHCARD_TERM + " TEXT NOT NULL" + ", " + flashcardEntry.COLUMN_FLASHCARD_FIRST_TRY_RESULT + " INTEGER NOT NULL" + ", " + flashcardEntry.COLUMN_FLASHCARD_RESULT + " INTEGER NOT NULL" + ", " + flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER + " INTEGER DEFAULT 0" + ", " + flashcardEntry.COLUMN_FLASHCARD_DEFINITION + " TEXT NOT NULL )";
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
