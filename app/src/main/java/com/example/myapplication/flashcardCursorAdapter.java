package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.data.flashcardData.flashcardContract.flashcardEntry;


public class flashcardCursorAdapter extends CursorAdapter {


    public flashcardCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.flashcard_list_item, parent,false);
    }

    long mLastClickTime = 0;
    long mLastClickTime1 = 0;
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView term = view.findViewById(R.id.flashcard_listitem_term);
        TextView definition = view.findViewById(R.id.flashcard_listitem_definition);

        String cursorTerm = cursor.getString(cursor.getColumnIndexOrThrow(flashcardEntry.COLUMN_FLASHCARD_TERM));
        String cursorDefinition = cursor.getString(cursor.getColumnIndexOrThrow(flashcardEntry.COLUMN_FLASHCARD_DEFINITION));

        ImageButton mListenButton = view.findViewById(R.id.flashcard_listen);
        mListenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FlashcardActivity){
                    ((FlashcardActivity) context).tts(cursorDefinition);
                }
                else {
                    ((flashcardStudyFinishActivity) context).tts(cursorDefinition);
                }
            }
        });
        //Menu button
        long flashcard_id = cursor.getLong(cursor.getColumnIndex(flashcardEntry._ID));
        ImageButton flashcard_menu = view.findViewById(R.id.flashcard_menu);
        if (context instanceof flashcardStudyFinishActivity){
            flashcard_menu.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mListenButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mListenButton.setLayoutParams(params);
        }
        else{
            flashcard_menu.setOnClickListener(new View.OnClickListener() {
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
                                case R.id.flashcard_menu_update:
                                    //Call update method in MainActivity
                                    ((FlashcardActivity) context).flashcard_menu_update_dialog(flashcard_id, cursorTerm, cursorDefinition);
                                    break;

                                //Delete
                                case R.id.flashcard_menu_delete:
                                    //Call delete method in MainActivity
                                    ((FlashcardActivity) context).flashcard_menu_delete(flashcard_id);
                                    break;

                            }
                            return true;
                        }
                    });
                    menu.inflate (R.menu.flashcard_menu);
                    menu.show();
                }
            });

        }






        term.setText(cursorTerm);
        definition.setText(cursorDefinition);

    }



}
