package com.john.kalimeris.notes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class NotesWidget extends AppWidgetProvider {

    private static ListView listView;
    private static LayoutInflater inflater;
    private static ArrayList<noteObject> _myDataset;
    public static final String ACTION_CLICK = "com.john.kalimeris.notes.ACTION_CLICK";
    public static final String EXTRA_ITEM = "com.john.kalimeris.notes.EXTRA_ITEM";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews mView = new RemoteViews(context.getPackageName(),
                R.layout.notes_widget);

        countNotes(context);
        mView.setTextViewText(R.id.txvWidgetTitle, countNotes(context) + " Notes, " + countAlarms(context) + " Alarms");

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(appWidgetId, R.id.listView, intent);

        Intent detailIntent = new Intent(ACTION_CLICK);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mView.setPendingIntentTemplate(R.id.listView, pIntent);

        Intent newNoteIntent = new Intent(context, NewEditNoteActivity.class);
        newNoteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newNoteIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        newNoteIntent.setData(Uri.parse(newNoteIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingNewNoteIntent = PendingIntent.getActivity(context, 0, newNoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mView.setOnClickPendingIntent(R.id.imageButtonPlus, pendingNewNoteIntent);

        appWidgetManager.updateAppWidget(appWidgetId, mView);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
    }

    private static int countNotes(Context context) {
        NotesDataSource dbAdapter = new NotesDataSource(context);
        dbAdapter.open();
        int numberNotes = dbAdapter.getAllNotes().size();
        dbAdapter.close();

        return numberNotes;
    }

    private static int countAlarms(Context context) {
        NotesDataSource dbAdapter = new NotesDataSource(context);
        dbAdapter.open();
        int numberAlarms = dbAdapter.countAlarms();
        dbAdapter.close();

        return numberAlarms;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_CLICK)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            long idNote = intent.getLongExtra(EXTRA_ITEM, -1);
            noteObject note = getNote(context, idNote);
            if (note != null)
                Utils.intentNewNoteActivity(context, note);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private noteObject getNote(Context context, long idNote) {

        NotesDataSource dbAdapter = new NotesDataSource(context);
        dbAdapter.open();
        noteObject note = dbAdapter.getNoteFromId(idNote);
        dbAdapter.close();

        return note;
    }
}