package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;
import com.example.myapplication.data.flashcardSetData.flashcardSetContract.flashcardSetEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Spell it!");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FloatingActionButton fab = findViewById(R.id.fab_flashcard_set_activity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(MainActivity.this);
            }
        });
        displayDatabaseInfo();
    }
    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    public class ViewDialog{
        public void showDialog(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_create_flashcard_set);

            Button mDialogYes = dialog.findViewById(R.id.loo_flashcard_set);
            mDialogYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText mNameEditText = dialog.findViewById(R.id.flashcard_set_dialog_name);
                    if (TextUtils.isEmpty(mNameEditText.getText().toString())){
                        dialog.dismiss();
                        return;
                    }
                    String name = mNameEditText.getText().toString().trim();

                    ContentValues values = new ContentValues();
                    values.put(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME, name);
                    values.put(flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND, "0");

                    getContentResolver().insert(flashcardSetEntry.CONTENT_URI, values);



                    dialog.dismiss();
                    displayDatabaseInfo();
                }
            });
            Button mDialogNo = dialog.findViewById(R.id.katkesta_flashcard_set);
            mDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    private void displayDatabaseInfo() {

        String[] projection = {flashcardSetEntry._ID, flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME, flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND};
        Cursor cursor = getContentResolver().query(flashcardSetEntry.CONTENT_URI, projection, null, null, null);

        flashcardSetCursorAdapter flashcardSetCursorAdapter = new flashcardSetCursorAdapter(this, cursor);

        ListView flashcardSetsListView = (ListView) findViewById(R.id.flashCardSets_listview);

        View emptyView = findViewById(R.id.flashcard_set_empty_view);
        flashcardSetsListView.setEmptyView(emptyView);
        
        flashcardSetsListView.setAdapter(flashcardSetCursorAdapter);
        flashcardSetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(MainActivity.this, FlashcardActivity.class);
                Uri currentFlashcardSetUri = ContentUris.withAppendedId(flashcardSetEntry.CONTENT_URI, id);
                intent.setData(currentFlashcardSetUri);
                startActivity(intent);

            }
        });
        flashcardSetsListView.setBackgroundColor(Color.WHITE);
        flashcardSetsListView.setAdapter(flashcardSetCursorAdapter);
    }
    public void flashcard_set_menu_update_dialog(long flashcard_set_id, String flashcard_set_name){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_create_flashcard_set);

        TextView textView = dialog.findViewById(R.id.flashcard_set_dialog_textview);
        textView.setText("Muuda sõnakaartide grupi nimi");

        EditText mNameEditText = dialog.findViewById(R.id.flashcard_set_dialog_name);
        mNameEditText.setText(flashcard_set_name);

        Button mDialogYes = dialog.findViewById(R.id.loo_flashcard_set);
        mDialogYes.setText("Muuda");
        mDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                EditText mNameEditText = (EditText) dialog.findViewById(R.id.flashcard_set_dialog_name);
                if (TextUtils.isEmpty(mNameEditText.getText().toString())){
                    dialog.dismiss();
                    return;
                }
                String name = mNameEditText.getText().toString().trim();

                ContentValues values = new ContentValues();
                values.put(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME, name);
                String selectionFlashcardSet = flashcardSetEntry.flashcardSetId + "=?";
                String[] selectionArgs = {String.valueOf(flashcard_set_id)};

                getContentResolver().update(flashcardSetEntry.CONTENT_URI, values, selectionFlashcardSet, selectionArgs);

                dialog.dismiss();
                displayDatabaseInfo();
            }
        });
        Button mDialogNo = dialog.findViewById(R.id.katkesta_flashcard_set);
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
    public void flashcard_set_menu_delete(long flashcard_set_id){
        String selectionFlashcardSet = flashcardSetEntry.flashcardSetId + "=?";
        String selectionFlashcard = flashcardEntry.FLASHCARD_SET_ID + "=?";
        String[] selectionArgs = {String.valueOf(flashcard_set_id)};
        getContentResolver().delete(flashcardSetEntry.CONTENT_URI, selectionFlashcardSet, selectionArgs);
        getContentResolver().delete(flashcardEntry.CONTENT_URI, selectionFlashcard, selectionArgs);
        displayDatabaseInfo();

    }
    public void onBackPressed(){
        this.finishAffinity();
    }


}