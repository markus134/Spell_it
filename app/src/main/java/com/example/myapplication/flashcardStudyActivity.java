package com.example.myapplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;
import com.example.myapplication.data.flashcardData.flashcardDbHelper;
import com.example.myapplication.data.flashcardSetData.flashcardSetContract;

public class flashcardStudyActivity extends AppCompatActivity {
    flashcardDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_study);
        setTitle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startPractice();

    }
    static String definition;
    static String definitionAnswer;
    static String term;
    static int groupNumber;
    static int _id;
    static int position = 0;
    static Cursor cursor;
    String flashcard_set_id;



    private void startPractice(){
        Uri uri = getIntent().getData();
        flashcard_set_id = String.valueOf(ContentUris.parseId(uri));

        String intent = getIntent().getStringExtra("intent");
        String change = getIntent().getStringExtra("change");
        if (intent != null){
            if (intent.equals("cursorAdapter")){


                if (change.equals("beginning")){
                    String newSelection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
                    String[] newSelectionArgs = {flashcard_set_id};
                    ContentValues newValues = new ContentValues();
                    newValues.put(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND, "0");
                    getContentResolver().update(flashcardSetContract.flashcardSetEntry.CONTENT_URI, newValues, newSelection, newSelectionArgs);

                    String selection = flashcardEntry.FLASHCARD_SET_ID + "=?";
                    String[] selectionArgs = {flashcard_set_id};
                    ContentValues values = new ContentValues();
                    values.put(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER, "0");
                    getContentResolver().update(flashcardEntry.CONTENT_URI, values, selection, selectionArgs);
                }


            }
        }


        mDbHelper = new flashcardDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT " + flashcardEntry._ID + ", " + flashcardEntry.COLUMN_FLASHCARD_TERM + ", " + flashcardEntry.COLUMN_FLASHCARD_DEFINITION + ", " + flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER + ", " + flashcardEntry.FLASHCARD_SET_ID
                + " FROM " + flashcardEntry.TABLE_NAME
                + " WHERE " + flashcardEntry.FLASHCARD_SET_ID + " = ?"
                + " AND " + flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER + " < 3" + " ORDER BY RANDOM()",
                new  String[] {flashcard_set_id});
        position = 0;
        updateInfo();
        }
    private void updateInfo() {
        int cursorCount = cursor.getCount();
        if (cursorCount == position){
            cursor.close();

            String[] projection = {flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND};
            String selection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
            String[] selectionArgs = {flashcard_set_id};
            Cursor cursor1 = getContentResolver().query(flashcardSetContract.flashcardSetEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            cursor1.moveToFirst();

            int currentRound = cursor1.getInt(cursor1.getColumnIndex(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND));

            String newSelection = flashcardSetContract.flashcardSetEntry.flashcardSetId + "=?";
            String[] newSelectionArgs = {flashcard_set_id};
            ContentValues contentValues = new ContentValues();
            contentValues.put(flashcardSetContract.flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND, currentRound + 1);
            cursor1.close();

            getContentResolver().update(flashcardSetContract.flashcardSetEntry.CONTENT_URI, contentValues, newSelection, newSelectionArgs);

            Intent intent = new Intent(flashcardStudyActivity.this, flashcardStudyFinishActivity.class);
            Uri currentFlashcardSetUri = ContentUris.withAppendedId(flashcardSetContract.flashcardSetEntry.CONTENT_URI, Long.parseLong(flashcard_set_id));
            intent.setData(currentFlashcardSetUri);
            startActivity(intent);
        }
        else if (position < cursorCount){
            cursor.moveToPosition(position);
            definition = cursor.getString(cursor.getColumnIndex(flashcardEntry.COLUMN_FLASHCARD_DEFINITION));
            term = cursor.getString(cursor.getColumnIndex(flashcardEntry.COLUMN_FLASHCARD_TERM));
            groupNumber = cursor.getInt(cursor.getColumnIndex(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER));
            _id = cursor.getInt(cursor.getColumnIndex(flashcardEntry._ID));
            position++;
            updateUi();
        }
    }

    private void updateUi() {
        TextView mTermTextView = (TextView) findViewById(R.id.flashcard_study_term);
        EditText mDefinitionEditText = (EditText) findViewById(R.id.flashcard_study_definition);

        mTermTextView.setText(term);
        mDefinitionEditText.setText("");

        buttonListener();

    }
    private void buttonListener() {
        Button mSubmitButton = (Button) findViewById(R.id.flashcard_study_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mDefinitionEditText = (EditText) findViewById(R.id.flashcard_study_definition);
                String definitionAnswer = mDefinitionEditText.getText().toString().trim();

                if (definition.equals(definitionAnswer)) {
                    ContentValues values = new ContentValues();
                    values.put(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER, String.valueOf(groupNumber + 1));
                    values.put(flashcardEntry.COLUMN_FLASHCARD_RESULT, "0"); // 0 result means correct answer
                    String newSelection = flashcardEntry._ID + "=?";
                    String[] newSelectionArgs = {String.valueOf(_id)};

                    getContentResolver().update(flashcardEntry.CONTENT_URI, values, newSelection, newSelectionArgs);
                    updateInfo();
                }
                else {
                    ContentValues values = new ContentValues();
                    if (groupNumber != 0){
                        values.put(flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER, String.valueOf(groupNumber - 1));
                    }
                    values.put(flashcardEntry.COLUMN_FLASHCARD_RESULT, "1"); //1 result means incorrect answer
                    String newSelection = flashcardEntry._ID + "=?";
                    String[] newSelectionArgs = {String.valueOf(_id)};

                    getContentResolver().update(flashcardEntry.CONTENT_URI, values, newSelection, newSelectionArgs);
                    displayWrongAnswer();
                }
            }
        });
    }
    private void displayWrongAnswer(){
        TextView mYourAnswerUpper = findViewById(R.id.flashcard_study_your_upper_answer);
        TextView mYourAnswerLower = findViewById(R.id.flashcard_study_your_lower_answer);
        TextView mCorrectAnswerUpper = findViewById(R.id.flashcard_study_upper_correct_answer);
        TextView mCorrectAnswerLower = findViewById(R.id.flashcard_study_lower_correct_answer);
        Button mSubmitButton = findViewById(R.id.flashcard_study_submit);
        EditText mDefinitionEditText = findViewById(R.id.flashcard_study_definition);

        definitionAnswer = mDefinitionEditText.getText().toString().trim();

        mDefinitionEditText.setText("");
        mYourAnswerUpper.setText("Teie vastus:");
        mYourAnswerLower.setText(definitionAnswer);
        mCorrectAnswerUpper.setText("Õige vastus:");
        mCorrectAnswerLower.setText(definition);
        mSubmitButton.setText("JÄTKA");
        ViewCompat.setBackgroundTintList(mDefinitionEditText, ColorStateList.valueOf(getResources().getColor(R.color.red)));

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                definitionAnswer = mDefinitionEditText.getText().toString().trim();
                if (definition.equals(definitionAnswer)){
                    ViewCompat.setBackgroundTintList(mDefinitionEditText, ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                    mCorrectAnswerUpper.setText("");
                    mCorrectAnswerLower.setText("");
                    mYourAnswerLower.setText("");
                    mYourAnswerUpper.setText("");
                    updateInfo();
                }
                else {
                    Toast toast = Toast.makeText(flashcardStudyActivity.this, "Palun sisestage õige vastus", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 60);
                    toast.show();
                }
            }
        });
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
            getSupportActionBar().setTitle(flashcard_set_name);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onFinish();
                Intent intent = new Intent(flashcardStudyActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        onFinish();
        Intent intent = new Intent(flashcardStudyActivity.this, MainActivity.class);
        startActivity(intent);

    }
    private void onFinish(){
        String selection2 = flashcardEntry.FLASHCARD_SET_ID + "=?";
        String[] selectionArgs2 = {flashcard_set_id};
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(flashcardEntry.COLUMN_FLASHCARD_RESULT, "2");
        getContentResolver().update(flashcardEntry.CONTENT_URI, contentValues2, selection2, selectionArgs2);
    }

}
