package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;
import com.example.myapplication.data.flashcardSetData.flashcardSetContract.flashcardSetEntry;

public class flashcardSetCursorAdapter extends CursorAdapter {

    public flashcardSetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.flashcard_set_list_item, parent,false);
    }
    long mLastClickTime = 0;
    long mLastClickTime1 = 0;
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Get textviews from listview
        TextView name = view.findViewById(R.id.flashcardSetName);
        TextView flashcardAmount = view.findViewById(R.id.flashcard_amount);

        //Get the values needed from cursor
        long flashcard_set_id = cursor.getLong(cursor.getColumnIndex(flashcardSetEntry.flashcardSetId));
        String flashcard_set_name = cursor.getString(cursor.getColumnIndex(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME));

        //Get flashcard amount in set as a integer and string
        int flashcardAmountinSetInt = flashcardAmountInSet(cursor, context);
        String flashcardAmountInSet = String.valueOf(flashcardAmountInSet(cursor, context));


        String flashcard = " sõnakaarti";
        if (flashcardAmountinSetInt == 1){
            flashcard = " sõnakaart";
        }

       

        int currentRound = cursor.getInt(cursor.getColumnIndex(flashcardSetEntry.COLUMN_FLASHCARD_SET_ROUND));
        int position = cursor.getPosition();
        //Harjuta button
        Button button = view.findViewById(R.id.flashcard_set_harjuta);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Prevents button from being clicked twice
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                //Give a toast message if flashcard amount in set is lower than 2
                if (flashcardAmountinSetInt < 2){
                    Toast toast = Toast.makeText(context, "Harjutamiseks on vaja vähemalt kaht sõnakaarti. Palun lisage rohkem sõnakaarte.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 60);
                    toast.show();
                    return;
                }


                if (currentRound  == 0){
                    Intent intent = new Intent(context, flashcardStudyActivity.class);
                    Uri currentFlashcardSetUri = ContentUris.withAppendedId(flashcardSetEntry.CONTENT_URI, flashcard_set_id);
                    intent.setData(currentFlashcardSetUri);
                    intent.putExtra("intent", "cursorAdapter");
                    intent.putExtra("change", "beginning");
                    context.startActivity(intent);
                }
                else {
                    ViewDialog alert = new ViewDialog();
                    Activity activity = (Activity) context;
                    alert.showDialog(activity, cursor, context, position);
                }



            }
        });

        //Menu button
        ImageButton flashcard_set_menu = view.findViewById(R.id.flashcard_set_menu);
        flashcard_set_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                PopupMenu menu = new PopupMenu (context, v);

                menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (SystemClock.elapsedRealtime() - mLastClickTime1 < 1000){
                            return true;
                        }
                        mLastClickTime1 = SystemClock.elapsedRealtime();
                        int id = item.getItemId();
                        switch (id)
                        {
                            //Update
                            case R.id.flashcard_set_menu_update:
                                //Call update method in MainActivity
                                ((MainActivity) context).flashcard_set_menu_update_dialog(flashcard_set_id, flashcard_set_name);
                                break;

                            //Delete
                            case R.id.flashcard_set_menu_delete:
                                //Call delete method in MainActivity
                                ((MainActivity) context).flashcard_set_menu_delete(flashcard_set_id);
                                break;

                        }
                        return true;
                    }
                });
                menu.inflate (R.menu.flashcard_set_menu);
                menu.show();
            }
        });
        String cursorName = cursor.getString(cursor.getColumnIndexOrThrow(flashcardSetEntry.COLUMN_FLASHCARD_SET_NAME));

        name.setText(cursorName);
        flashcardAmount.setText(flashcardAmountInSet + flashcard);


    }
    private int flashcardAmountInSet(Cursor cursor, Context context){
        String flashcard_set_id = cursor.getString(cursor.getColumnIndexOrThrow(flashcardSetEntry._ID));
        String[] projection = {flashcardEntry.FLASHCARD_SET_ID, flashcardEntry._ID};
        String selection = flashcardEntry.FLASHCARD_SET_ID + "=?";
        String[] selectionArgs = {flashcard_set_id};
        Cursor cursor1 = context.getContentResolver().query(flashcardEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        int flashcardAmountInSet = cursor1.getCount();
        return flashcardAmountInSet;
    }
    public class ViewDialog{
        public void showDialog(Activity activity, Cursor cursor, Context context, int position){
            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_confirm_practice);

            Button mDialogContinue = dialog.findViewById(R.id.flashcard_continue_practice_dialog);
            mDialogContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    cursor.moveToPosition(position);
                    String flashcard_set_id = cursor.getString(cursor.getColumnIndexOrThrow(flashcardSetEntry._ID));
                    Intent intent = new Intent(context, flashcardStudyActivity.class);
                    Uri currentFlashcardSetUri = ContentUris.withAppendedId(flashcardSetEntry.CONTENT_URI, Long.parseLong(flashcard_set_id));
                    intent.setData(currentFlashcardSetUri);
                    intent.putExtra("intent", "cursorAdapter");
                    intent.putExtra("change", "continue");
                    context.startActivity(intent);
                }
            });
            Button mDialogStartFromBeginning = dialog.findViewById(R.id.flashcard_start_beginning_dialog);
            mDialogStartFromBeginning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    cursor.moveToPosition(position);
                    String flashcard_set_id = cursor.getString(cursor.getColumnIndexOrThrow(flashcardSetEntry._ID));
                    Intent intent = new Intent(context, flashcardStudyActivity.class);
                    Uri currentFlashcardSetUri = ContentUris.withAppendedId(flashcardSetEntry.CONTENT_URI, Long.parseLong(flashcard_set_id));
                    intent.setData(currentFlashcardSetUri);
                    intent.putExtra("intent", "cursorAdapter");
                    intent.putExtra("change", "beginning");
                    context.startActivity(intent);
                }
            });
            dialog.show();
        }
    }

}

