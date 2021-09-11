package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.flashcardData.flashcardContract;
import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;
import com.example.myapplication.data.flashcardSetData.flashcardSetContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import java.util.Objects;

public class FlashcardActivity extends AppCompatActivity {
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        FloatingActionButton fab =  findViewById(R.id.fab_flashcard_set_activity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(FlashcardActivity.this);
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        showTitle();
        displayFlashcardDatabaseInfo();

    }
    public class ViewDialog{
        public void showDialog(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_create_flashcard);

            Button mDialogYes = dialog.findViewById(R.id.loo_flashcard);
            mDialogYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = getIntent().getData();
                    int flashcard_set_id = (int) ContentUris.parseId(uri);

                    EditText mTermEditText = dialog.findViewById(R.id.flashcard_dialog_term);
                    String term = mTermEditText.getText().toString().trim();

                    EditText mDefinitionEditText = dialog.findViewById(R.id.flashcard_dialog_definition);
                    String definition = mDefinitionEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(mTermEditText.getText().toString())){
                        dialog.dismiss();
                        return;
                    }
                    if (TextUtils.isEmpty(mDefinitionEditText.getText().toString())){
                        dialog.dismiss();
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put(flashcardContract.flashcardEntry.FLASHCARD_SET_ID, flashcard_set_id);
                    values.put(flashcardContract.flashcardEntry.COLUMN_FLASHCARD_TERM, term);
                    values.put(flashcardContract.flashcardEntry.COLUMN_FLASHCARD_DEFINITION, definition);
                    values.put(flashcardContract.flashcardEntry.COLUMN_FLASHCARD_GROUP_NUMBER, "0");
                    values.put(flashcardContract.flashcardEntry.COLUMN_FLASHCARD_RESULT, "2");

                    getContentResolver().insert(flashcardContract.flashcardEntry.CONTENT_URI, values);



                    dialog.dismiss();
                    displayFlashcardDatabaseInfo();

                }
            });
            Button mDialogNo = dialog.findViewById(R.id.katkesta_flashcard);
            mDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    private void displayFlashcardDatabaseInfo() {
        Uri uri = getIntent().getData();
        String flashcard_set_id = String.valueOf(ContentUris.parseId(uri));



        String[] projection = {flashcardContract.flashcardEntry._ID, flashcardContract.flashcardEntry.COLUMN_FLASHCARD_TERM, flashcardContract.flashcardEntry.COLUMN_FLASHCARD_DEFINITION};
        String selection = flashcardContract.flashcardEntry.FLASHCARD_SET_ID + "=?";
        String[] selectionArgs = {flashcard_set_id};
        Cursor cursor = getContentResolver().query(flashcardContract.flashcardEntry.CONTENT_URI, projection, selection, selectionArgs, null);

        flashcardCursorAdapter flashcardCursorAdapter = new flashcardCursorAdapter(this, cursor);

        ListView flashcardsListView = findViewById(R.id.flashcard_listview);
        flashcardsListView.setBackgroundColor(Color.WHITE);


        flashcardsListView.setAdapter(flashcardCursorAdapter);
    }
    private void showTitle(){

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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    long mLastClickTime = 0;
    public void flashcard_menu_update_dialog(long flashcard_id, String term, String definition){
        final Dialog dialog = new Dialog(FlashcardActivity.this);
        dialog.setContentView(R.layout.dialog_create_flashcard);

        EditText mTermEditText = dialog.findViewById(R.id.flashcard_dialog_term);
        mTermEditText.setText(term);

        EditText mDefinitionEditText = dialog.findViewById(R.id.flashcard_dialog_definition);
        mDefinitionEditText.setText(definition);

        Button mDialogYes = dialog.findViewById(R.id.loo_flashcard);
        mDialogYes.setText("Muuda");
        mDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                EditText mTermEditText = dialog.findViewById(R.id.flashcard_dialog_term);
                if (TextUtils.isEmpty(mTermEditText.getText().toString())){
                    dialog.dismiss();
                    return;
                }
                EditText mDefinitionEditText = dialog.findViewById(R.id.flashcard_dialog_definition);
                if (TextUtils.isEmpty(mDefinitionEditText.getText().toString())){
                    dialog.dismiss();
                    return;
                }
                String newTerm = mTermEditText.getText().toString().trim();
                String newDefinition = mDefinitionEditText.getText().toString().trim();


                ContentValues values = new ContentValues();
                values.put(flashcardEntry.COLUMN_FLASHCARD_TERM, newTerm);
                values.put(flashcardEntry.COLUMN_FLASHCARD_DEFINITION, newDefinition);
                String selectionFlashcard = flashcardEntry._ID + "=?";
                String[] selectionArgs = {String.valueOf(flashcard_id)};

                getContentResolver().update(flashcardEntry.CONTENT_URI, values, selectionFlashcard, selectionArgs);

                dialog.dismiss();
                displayFlashcardDatabaseInfo();
            }
        });
        Button mDialogNo = dialog.findViewById(R.id.katkesta_flashcard);
        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                dialog.dismiss();
            }
        });
        dialog.show();


    }
    public void flashcard_menu_delete(long flashcard_id){
        String selectionFlashcard = flashcardEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(flashcard_id)};
        getContentResolver().delete(flashcardEntry.CONTENT_URI, selectionFlashcard, selectionArgs);
        displayFlashcardDatabaseInfo();

    }


}
