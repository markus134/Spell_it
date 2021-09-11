package com.example.myapplication.data.flashcardSetData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.data.flashcardSetData.flashcardSetContract.flashcardSetEntry;

public class flashcardSetDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "flashcardSets.db";
    public static final int DATABASE_VERSION = 1;

    public flashcardSetDbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + flashcardSetEntry.TABLE_NAME + " (" + flashcardSetEntry.flashcardSetId + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " + flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME + " TEXT NOT NULL"  + ", " + flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND + " INTEGER NOT NULL" + " )";
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }



}
