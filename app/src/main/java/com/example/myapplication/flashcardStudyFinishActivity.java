package com.example.myapplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.flashcardData.flashcardContract;
import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;
import com.example.myapplication.data.flashcardSetData.flashcardSetContract;
import com.example.myapplication.data.flashcardData.flashcardDbHelper;


import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;


public class flashcardStudyFinishActivity extends AppCompatActivity {
    flashcardDbHelper mDbHelper;
    static Cursor cursorCorrectAnswer;
    static Cursor cursorIncorrectAnswer;
    static String flashcard_set_id;
    static String CorrectAnswerCount;
    static String IncorrectAnswerCount;
    static public int flashcardCount;
    static int groupNumber;
    boolean isThisTheFinalRound;
    TextToSpeech tts;
    int finalRoundFlashcardAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_study_finish);
        setTitle();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        mDbHelper = new flashcardDbHelper(this);

        displayFlashcardCorrectAnswerDatabaseInfo();
        displayFlashCardIncorrectAnswerDatabaseInfo();
        IsThisTheFinalRound();
        displayDatabaseScoreInfo();

        ListView mCorrectAnswerListview = findViewById(R.id.flashcard_study_finish_correct_listview);
        ListView mIncorrectAnswerListview = findViewById(R.id.flashcard_study_finish_incorrect_listview);

        setListViewHeightBasedOnChildren(mCorrectAnswerListview);
        setListViewHeightBasedOnChildren(mIncorrectAnswerListview);

        buttonListeners();

    }
    private void setTitle(){
        Uri uri = getIntent().getData();
        String flashcard_set_id = String.valueOf(ContentUris.parseId(uri));

        String[] projection = {flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME};
        String selection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
        String[] selectionArgs = {flashcard_set_id};
        Cursor cursor = getContentResolver().query(flashcardSetContract.flashcardSetEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor.moveToFirst()){
            String flashcard_set_name = cursor.getString(cursor.getColumnIndex(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME));
            Objects.requireNonNull(getSupportActionBar()).setTitle(flashcard_set_name);
            cursor.close();
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            onFinish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void displayFlashcardCorrectAnswerDatabaseInfo() {
        Uri uri = getIntent().getData();
        flashcard_set_id = String.valueOf(ContentUris.parseId(uri));

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        cursorCorrectAnswer = db.rawQuery("SELECT " + flashcardEntry._ID + ", " + flashcardEntry.COLUMN_FLASHCARD_TERM + ", " + flashcardEntry.COLUMN_FLASHCARD_DEFINITION + " FROM " + flashcardEntry.TABLE_NAME + " WHERE " + flashcardEntry.FLASHCARD_SET_ID + " = ?" + " AND " + flashcardEntry.COLUMN_FLASHCARD_RESULT + " = ?", new  String[] {flashcard_set_id, "0"});
        flashcardCursorAdapter flashcardCursorAdapter = new flashcardCursorAdapter(this, cursorCorrectAnswer);

        CorrectAnswerCount = String.valueOf(cursorCorrectAnswer.getCount());
        if (Integer.parseInt(CorrectAnswerCount) == 1){
            cursorCorrectAnswer.moveToFirst();
            String flashcard_id = cursorCorrectAnswer.getString(cursorCorrectAnswer.getColumnIndex(flashcardEntry._ID));

            String[] projection2 = {flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER};
            String selection2 = flashcardEntry._ID + "=?";
            String[] selectionArgs2 = {flashcard_id};
            Cursor cursor2 = getContentResolver().query(flashcardEntry.CONTENT_URI, projection2, selection2, selectionArgs2, null);
            cursor2.moveToFirst();

            groupNumber = cursor2.getInt(cursor2.getColumnIndex(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER));
            cursor2.close();
        }

        if (Integer.parseInt(CorrectAnswerCount) != 0){
            TextView mCorrectAnswerCountTextView = findViewById(R.id.flashcard_study_finish_correct_answer_count);
            if (Integer.parseInt(CorrectAnswerCount) == 1){
                mCorrectAnswerCountTextView.setText(CorrectAnswerCount + " ÕIGE");
            }
            else{
                mCorrectAnswerCountTextView.setText(CorrectAnswerCount + " ÕIGET");
            }


            ListView flashcardsListView = findViewById(R.id.flashcard_study_finish_correct_listview);
            flashcardsListView.setBackgroundColor(Color.WHITE);


            flashcardsListView.setAdapter(flashcardCursorAdapter);
        }
    }
    private void displayFlashCardIncorrectAnswerDatabaseInfo(){
        Uri uri = getIntent().getData();
        String flashcard_set_id = String.valueOf(ContentUris.parseId(uri));

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        cursorIncorrectAnswer = db.rawQuery("SELECT " + flashcardEntry._ID + ", " + flashcardEntry.COLUMN_FLASHCARD_TERM + ", " + flashcardEntry.COLUMN_FLASHCARD_DEFINITION + " FROM " + flashcardEntry.TABLE_NAME + " WHERE " + flashcardEntry.FLASHCARD_SET_ID + " = ?" + " AND " + flashcardEntry.COLUMN_FLASHCARD_RESULT + " = ?", new  String[] {flashcard_set_id, "1"});
        flashcardCursorAdapter flashcardCursorAdapter = new flashcardCursorAdapter(this, cursorIncorrectAnswer);

        IncorrectAnswerCount = String.valueOf(cursorIncorrectAnswer.getCount());
        if (Integer.parseInt(IncorrectAnswerCount) != 0){
            TextView mIncorrectAnswerCountTextView = findViewById(R.id.flashcard_study_finish_incorrect_answer_count);
            if (Integer.parseInt(IncorrectAnswerCount) == 1){
                mIncorrectAnswerCountTextView.setText(IncorrectAnswerCount + " VALE");
            }
            else{
                mIncorrectAnswerCountTextView.setText(IncorrectAnswerCount + " VALET");
            }


            ListView flashcardsListView = (ListView) findViewById(R.id.flashcard_study_finish_incorrect_listview);
            flashcardsListView.setBackgroundColor(Color.WHITE);


            flashcardsListView.setAdapter(flashcardCursorAdapter);
        }
        else {
            ListView mIncorrectAnswerListView = findViewById(R.id.flashcard_study_finish_incorrect_listview);
            TextView mIncorrectAnswerTextView = findViewById(R.id.flashcard_study_finish_incorrect_answer_count);

            mIncorrectAnswerTextView.setVisibility(View.GONE);
            mIncorrectAnswerListView.setVisibility(View.GONE);
        }

    }
    private void displayDatabaseScoreInfo(){
        int CorrectAnswerCount = cursorCorrectAnswer.getCount();
        int AnswerCount = CorrectAnswerCount + cursorIncorrectAnswer.getCount();

        float percent = (float) CorrectAnswerCount / (float) AnswerCount * 100;
        BigDecimal bigDecimal = new BigDecimal(Float.toString(percent));
        bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();


        String scoreText = CorrectAnswerCount + "/" + AnswerCount + " ehk " + bigDecimal.toPlainString() + "%";

        TextView mScoreTextView = findViewById(R.id.flashcard_study_finish_score);
        mScoreTextView.setText(scoreText);
        if (isThisTheFinalRound){
            TextView mRoundTextView = findViewById(R.id.flashcard_study_finish_round);
            mRoundTextView.setText("Palju õnne! Te olete õppinud kõik sõnad ära.");
            mRoundTextView.setGravity(Gravity.CENTER);
            mRoundTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

            TextView mResultText = findViewById(R.id.flashcard_study_finish_result_text);
            mResultText.setText("Teie viimase vooru tulemus oli:");

            Button mButtonContinue = findViewById(R.id.flashcard_study_finish_continue);
            mButtonContinue.setVisibility(View.GONE);

        }
        else
        {
            Uri uri = getIntent().getData();
            String flashcard_set_id = String.valueOf(ContentUris.parseId(uri));

            String[] projection = {flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND};
            String selection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
            String[] selectionArgs = {flashcard_set_id};
            Cursor cursor1 = getContentResolver().query(flashcardSetContract.flashcardSetEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            cursor1.moveToFirst();

            int currentRound = cursor1.getInt(cursor1.getColumnIndex(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND));
            cursor1.close();

            TextView mRoundTextView = findViewById(R.id.flashcard_study_finish_round);
            mRoundTextView.setText(currentRound + ". vooru lõpp");
        }

    }
    //Set listview heights based on their children
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private void buttonListeners(){
        Button mButtonContinue = findViewById(R.id.flashcard_study_finish_continue);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If this is the final round
                if (isThisTheFinalRound){
                    String selection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
                    String[] selectionArgs = {flashcard_set_id};
                    ContentValues values = new ContentValues();
                    values.put(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND, 0); // Reset group number to 0 after the final round is completed
                    getContentResolver().update(flashcardSetContract.flashcardSetEntry.CONTENT_URI, values, selection, selectionArgs);

                    String selection1 = flashcardEntry.FLASHCARD_SET_ID + "=?";
                    String[] selectionArgs1 = {flashcard_set_id};
                    ContentValues values1 = new ContentValues();
                    values1.put(flashcardEntry.COLUMN_FLASHCARD_FIRST_TRY_RESULT, 0); //Reset first try result to 0 after the final round is completed
                    getContentResolver().update(flashcardEntry.CONTENT_URI, values1, selection1, selectionArgs1);

                    Intent intent = new Intent(flashcardStudyFinishActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                //If this is not the final round
                else{
                    Intent intent = new Intent(flashcardStudyFinishActivity.this, flashcardStudyActivity.class);
                    Uri currentFlashcardSetUri = ContentUris.withAppendedId(flashcardSetContract.flashcardSetEntry.CONTENT_URI, Long.parseLong(flashcard_set_id));
                    intent.setData(currentFlashcardSetUri);
                    startActivity(intent);

                    String selection1 = flashcardEntry.FLASHCARD_SET_ID + "=?";
                    String[] selectionArgs1 = {flashcard_set_id};
                    ContentValues values1 = new ContentValues();
                    values1.put(flashcardEntry.COLUMN_FLASHCARD_RESULT, 2);
                    getContentResolver().update(flashcardEntry.CONTENT_URI, values1, selection1, selectionArgs1);

                    String selection2 = flashcardEntry.FLASHCARD_SET_ID + "=?" + " AND " +flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER +"=?";
                    String[] selectionArgs2 = {flashcard_set_id, "3"};
                    ContentValues values2 = new ContentValues();
                    values2.put(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER, 4);
                    getContentResolver().update(flashcardEntry.CONTENT_URI, values2, selection2, selectionArgs2);

                }
            }
        });
        Button mButtonCancel = findViewById(R.id.flashcard_study_finish_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
                Intent intent = new Intent(flashcardStudyFinishActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    //If back button gets pressed
    public void onBackPressed(){
        onFinish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    //On finish reset result info
    private void onFinish(){
        String selection2 = flashcardEntry.FLASHCARD_SET_ID + "=?";
        String[] selectionArgs2 = {flashcard_set_id};
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(flashcardEntry.COLUMN_FLASHCARD_RESULT, "2");
        getContentResolver().update(flashcardEntry.CONTENT_URI, contentValues2, selection2, selectionArgs2);


        if (Integer.parseInt(CorrectAnswerCount) == finalRoundFlashcardAmount && Integer.parseInt(IncorrectAnswerCount) == 0) {
            String selection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
            String[] selectionArgs = {flashcard_set_id};
            ContentValues values = new ContentValues();
            values.put(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND, 0);
            getContentResolver().update(flashcardSetContract.flashcardSetEntry.CONTENT_URI, values, selection, selectionArgs);

            String selection1 = flashcardEntry.FLASHCARD_SET_ID + "=?";
            String[] selectionArgs1 = {flashcard_set_id};
            ContentValues values1 = new ContentValues();
            values1.put(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER, 0);
            getContentResolver().update(flashcardEntry.CONTENT_URI, values1, selection1, selectionArgs1);
        }

    }
    public void tts(String text){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.US);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }
    public void IsThisTheFinalRound(){
        boolean LastCardCorrect;
        int CorrectAnswerCount = cursorCorrectAnswer.getCount();
        int IncorrectAnswerCount = cursorIncorrectAnswer.getCount();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursorFinalRound = db.rawQuery("SELECT " + flashcardEntry._ID + ", " + flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER + " FROM " + flashcardEntry.TABLE_NAME + " WHERE " + flashcardEntry.FLASHCARD_SET_ID + " = ?" + " AND " + flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER + " = ?", new  String[] {flashcard_set_id, "3"});
        if (cursorFinalRound == null){
            finalRoundFlashcardAmount = -1;
        }
        else{
            finalRoundFlashcardAmount = cursorFinalRound.getCount();
        }

        if (finalRoundFlashcardAmount == 1 && groupNumber == 3){
            LastCardCorrect = true;
        }
        else if (finalRoundFlashcardAmount != 1){
            LastCardCorrect = true;
        }
        else{
            LastCardCorrect = false;
        }
        if (CorrectAnswerCount == finalRoundFlashcardAmount && IncorrectAnswerCount == 0 && LastCardCorrect){
            isThisTheFinalRound = true;
        }
        else {
            isThisTheFinalRound = false;
        }

    }

}
