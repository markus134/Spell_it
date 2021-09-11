package com.example.myapplication.data.flashcardSetData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class flashcardSetContract {
    public static final String CONTENT_AUTHORITY = "com.example.myapplication.data.flashcardSetData";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FLASHCARD_SETS = "flashcardSets";

    public static abstract class flashcardSetEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FLASHCARD_SETS);

        public static final String TABLE_NAME = "flashcardSets";

        public static final String flashcardSetId = BaseColumns._ID;
        public static final String COLUMN_FLASHCARD_SET_NAME = "name";
        public static final String COLUMN_FLASHCARD_SET_ROUND = "round";

        //Mime type for list of flashcard sets
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLASHCARD_SETS;

       //Mime type for a single flashcard set
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLASHCARD_SETS;
    }
}
