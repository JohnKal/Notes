package com.john.kalimeris.notes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by John on 05/11/16.
 */

public class SuggestionsCursorAdapter extends CursorAdapter {

    public SuggestionsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView titleTxtV = (TextView) view.findViewById(R.id.textView);
        TextView descriptionTxtV = (TextView) view.findViewById(R.id.textView2);

        String title = cursor.getString(cursor.getColumnIndex("title"));
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

        titleTxtV.setText(title);
        descriptionTxtV.setText(description);
    }
}
