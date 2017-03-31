package com.john.kalimeris.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 02/10/16.
 */

public class NotesDataSource {

    private SQLiteDatabase database;
    private DatabaseHandler dbHelper;
    private String[] allColumns = { DatabaseHandler.COLUMN_ID,
            DatabaseHandler.COLUMN_TITLE, DatabaseHandler.COLUMN_DESCRIPTION,
            DatabaseHandler.COLUMN_DATE, DatabaseHandler.COLUMN_COLOR, DatabaseHandler.COLUMN_EVENT_ID,
            DatabaseHandler.COLUMN_CHECK};

    public NotesDataSource(Context context) {
        dbHelper = new DatabaseHandler(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createNote(noteObject note) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHandler.COLUMN_DESCRIPTION, note.getDescription());
        values.put(DatabaseHandler.COLUMN_DATE, note.getDate());
        values.put(DatabaseHandler.COLUMN_COLOR, note.getColor());
        values.put(DatabaseHandler.COLUMN_EVENT_ID, note.getEventId());
        values.put(DatabaseHandler.COLUMN_CHECK, note.getCheck() ? 1 : 0);

        long insertId = database.insert(DatabaseHandler.TABLE, null, values);

        return insertId;
    }

    public void insertMultipleNotes(List<noteObject> notesForInsert) {

        long insertId = 0;

        database.beginTransaction();

        try {
            for (noteObject note : notesForInsert) {
                insertId = createNote(note);
            }
            database.setTransactionSuccessful();
        }
        finally {
            database.endTransaction();
        }

    }

    public long updateNote(noteObject note) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.COLUMN_TITLE, note.getTitle());
        values.put(DatabaseHandler.COLUMN_DESCRIPTION, note.getDescription());
        values.put(DatabaseHandler.COLUMN_DATE, note.getDate());
        values.put(DatabaseHandler.COLUMN_COLOR, note.getColor());
        values.put(DatabaseHandler.COLUMN_EVENT_ID, note.getEventId());
        values.put(DatabaseHandler.COLUMN_CHECK, note.getCheck() ? 1 : 0);

        long updateId = database.update(DatabaseHandler.TABLE, values, DatabaseHandler.COLUMN_ID + " = ?", new String []{String.valueOf(note.getId())});

        return updateId;
    }

    public boolean deleteNote(long id)
    {
        return database.delete(DatabaseHandler.TABLE, DatabaseHandler.COLUMN_ID + " = " + id, null) > 0;
    }

    public boolean deleteAllNotes()
    {
        return database.delete(DatabaseHandler.TABLE, null, null) > 0;
    }

    public List<noteObject> getAllNotes() {
        List<noteObject> notes = new ArrayList<noteObject>();

        Cursor cursor = database.query(DatabaseHandler.TABLE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            noteObject note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notes;
    }

    public List<noteObject> getAllNotesWithEvents() {
        List<noteObject> notes = new ArrayList<noteObject>();

        Cursor cursor = database.query(DatabaseHandler.TABLE,
                allColumns, DatabaseHandler.COLUMN_EVENT_ID + " != -1", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            noteObject note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notes;
    }

    public noteObject getNoteFromId(long idNote) {
        noteObject note = null;

        Cursor cursor = database.query(DatabaseHandler.TABLE,
                allColumns, DatabaseHandler.COLUMN_ID + " == " + idNote, null, null, null, null);

        if (cursor.moveToFirst())
            note = cursorToNote(cursor);

        // make sure to close the cursor
        cursor.close();

        return note;
    }

    public int countAlarms() {
        int count = 0;

        Cursor cursor = database.query(DatabaseHandler.TABLE, allColumns, DatabaseHandler.COLUMN_EVENT_ID + " != -1", null, null, null, null);

        if (cursor.moveToFirst())
            count = cursor.getCount();

        // make sure to close the cursor
        cursor.close();
        return count;
    }

    private noteObject cursorToNote(Cursor cursor) {
        noteObject comment = new noteObject();
        comment.setId(cursor.getLong(0));
        comment.setTitle(cursor.getString(1));
        comment.setDescription(cursor.getString(2));
        comment.setDate(cursor.getString(3));
        comment.setColor(cursor.getString(4));
        comment.setEventId(cursor.getLong(5));
        comment.setCheck((cursor.getInt(6) == 1) ? true : false);
        return comment;
    }

    public Cursor cursorSuggestionList(String suggestTitle, String suggestDescription)
    {
        return database.rawQuery("Select rowid _id, title, description From notes Where title = ? Or description = ?", new String []{suggestTitle, suggestDescription});
    }
}
