package com.john.kalimeris.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by production_user13 on 30/9/2016.
 */
public class NoteItemWidgetAdapter extends ArrayAdapter<noteObject> {

    private ArrayList<noteObject> _notesObjects;
    private Context _context;
    private int _layout;

    public NoteItemWidgetAdapter(Context context, int resource, ArrayList<noteObject> objects) {
        super(context, resource, objects);
        _context = context;
        _layout = resource;
        _notesObjects = objects;
    }

    static class ViewHolder {
        public TextView _title;
        public TextView _date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(_layout, null, true);
            viewHolder = new ViewHolder();
            viewHolder._title = (TextView) rowView.findViewById(R.id.textView);
            viewHolder._date = (TextView) rowView.findViewById(R.id.textView2);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        noteObject noteObject = _notesObjects.get(position);
        viewHolder._title.setText(noteObject.getTitle());
        viewHolder._date.setText(noteObject.getDate());
        return rowView;
    }
}
