package com.john.kalimeris.notes;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 01/10/16.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    Context _context = null;
    private List<noteObject> _notesObjects;
    private int mAppWidgetId;
    private int _position = -1;

    public WidgetDataProvider(Context context, Intent intent) {
        _context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        _notesObjects = new ArrayList<noteObject>();
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return _notesObjects.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(_context.getPackageName(), R.layout.note_item_widget);
        views.setTextViewText(R.id.tvTitle, _notesObjects.get(position).getTitle());
        views.setTextViewText(R.id.tvDate, _notesObjects.get(position).getDate());
        views.setTextViewText(R.id.txtv_description, _notesObjects.get(position).getDescription());
        views.setViewVisibility(R.id.imageViewReminder, _notesObjects.get(position).getEventId() != -1 ? View.VISIBLE : View.INVISIBLE);

        //views.setInt(R.id.linearLayoutParent, "setBackgroundColor", Color.parseColor("#D3D3D3"));

        Intent intent = new Intent();
        intent.putExtra(NotesWidget.EXTRA_ITEM, _notesObjects.get(position).getId());
        views.setOnClickFillInIntent(R.id.linearLayoutParent, intent);

        _position = position;
        return views;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    private void initData() {

        _notesObjects.clear();
        NotesDataSource dbAdapter1 = new NotesDataSource(_context);
        dbAdapter1.open();
        _notesObjects = dbAdapter1.getAllNotes();
        dbAdapter1.close();
    }
}