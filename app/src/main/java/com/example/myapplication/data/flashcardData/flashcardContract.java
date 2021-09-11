package com.example.myapplication.data.flashcardData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class flashcardContract {
    public static final String CONTENT_AUTHORITY = "com.example.myapplication.data.flashcardData";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FLASHCARD_SETS = "flashcards";

    public static abstract class flashcardEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FLASHCARD_SETS);

        public static final String TABLE_NAME = "flashcards";

        public static final String _ID = BaseColumns._ID;
        public static final String FLASHCARD_SET_ID = "flashcardSetId";
        public static final String COLUMN_FLASHCARD_TERM = "term";
        public static final String COLUMN_FLASHCARD_DEFINITION = "definition";
        public static final String COLUMN_FLASHCARD_GROUP_NUMBER = "groupNumber";
        public static final String COLUMN_FLASHCARD_RESULT = "result";

        //Mime type for list of flashcards
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLASHCARD_SETS;

        //Mime type for a single flashcard
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLASHCARD_SETS;
    }
}
