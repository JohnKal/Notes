package com.john.kalimeris.notes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by John on 01/10/16.
 */

public class NewEditNoteActivity extends AppCompatActivity implements SpectrumPalette.OnColorSelectedListener, ShareActionProvider.OnShareTargetSelectedListener {

    Activity _activity;
    EditText title;
    EditText description;
    TextInputLayout floatingTitleLabel;
    TextInputLayout floatingDescriptionLabel;
    View _view;
    boolean newNote;
    long _idNote;
    private noteObject noteObject;
    String _colorCode = "#FF6961";
    long _eventId = -1;
    boolean _check = false;
    Toolbar toolbar;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_layout);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        _view = inflater.inflate(R.layout.new_note_layout, null, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        Utils.setFullScreen(this);

        Intent intent = getIntent();
        noteObject = (noteObject) intent.getParcelableExtra("noteObject");
        newNote = intent.getBooleanExtra("newNote", true);

        floatingTitleLabel = (TextInputLayout) findViewById(R.id.titleWrapper);
        floatingDescriptionLabel = (TextInputLayout) findViewById(R.id.descriptionWrapper);

        title = (EditText) findViewById(R.id.edTxtTitle);

        description = (EditText) findViewById(R.id.edTxtDesc);

        if (!newNote)
        {
            _idNote = noteObject.getId();
            title.setText(noteObject.getTitle());
            description.setText(noteObject.getDescription());
            getSupportActionBar().setSubtitle(noteObject.getTitle());
            _eventId = noteObject.getEventId();
            _check = noteObject.getCheck();
            if (_check) {
                setAlphaAndCheckNote(0.5f);
            }
        }

        floatingTitleLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 30) {
                    floatingTitleLabel.setError(getString(R.string.title_error_msg));
                    floatingTitleLabel.setErrorEnabled(true);
                } else if (text.length() == 0) {
                    floatingTitleLabel.setError(getString(R.string.title_missing_msg));
                    floatingTitleLabel.setErrorEnabled(true);
                }
                else
                    floatingTitleLabel.setErrorEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                if (_check) {
                    setAlphaAndCheckNote(1f);
                    _check = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        floatingDescriptionLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 300) {
                    floatingDescriptionLabel.setError(getString(R.string.desc_error_msg));
                    floatingDescriptionLabel.setErrorEnabled(true);
                }
                else
                    floatingTitleLabel.setErrorEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                if (_check) {
                    setAlphaAndCheckNote(1f);
                    _check = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        _activity = this;

        SpectrumPalette spectrumPalette = (SpectrumPalette) findViewById(R.id.palette);
        int[] colors = getResources().getIntArray(R.array.list_colors_int);
        String[] colorsString = getResources().getStringArray(R.array.list_colors_strings);
        spectrumPalette.setColors(colors);
        spectrumPalette.setOnColorSelectedListener(this);
        if (!newNote) {
            for (int i = 0; i < colorsString.length; i++) {
                if (colorsString[i].equalsIgnoreCase(noteObject.getColor()))
                    spectrumPalette.setSelectedColor(Color.parseColor(colorsString[i]));
            }
        } else {
            SharedPreferences sharedPrefs = android.preference.PreferenceManager.getDefaultSharedPreferences(_activity);
            int color = sharedPrefs.getInt("default_color", -1);
            spectrumPalette.setSelectedColor(color);
        }
    }

    @Override public void onColorSelected(@ColorInt int color) {
        _colorCode = String.format("#%06X", (0xFFFFFF & color));
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.delete);

        if (newNote) {
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        } else {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        }

        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_note_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.done:
                saveNote();
            case R.id.overflow:
                showOverflowMenu(item);
            /*case R.id.menu_item_share:
                if (!newNote) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, noteObject.getDescription());
                    mShareActionProvider.setShareIntent(intent);
                }*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showOverflowMenu(MenuItem item) {

        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.overflow));
        popupMenu.getMenuInflater().inflate(R.menu.overlow_menu, popupMenu.getMenu());
        enableMenuItems(popupMenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.delete)
                {
                    deleteNote();
                    return true;
                }
                else if (itemId == R.id.alarm)
                {
                    confirmationDateDialog();
                    return true;
                }
                else if (itemId == R.id.notalarm)
                {
                    deleteEventId(_eventId);
                    return true;
                }
                else if (itemId == R.id.check)
                {
                    if (!_check) {
                        _check = true;
                        setAlphaAndCheckNote(0.5f);
                        invalidateOptionsMenu();
                    }
                    else {
                        _check = false;
                        setAlphaAndCheckNote(1f);
                        invalidateOptionsMenu();
                    }
                    return true;
                }
                else if (itemId == R.id.share)
                {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<h1>" + noteObject.getTitle() + "</h1>" + " \n " + "<p>" + noteObject.getDescription() + "</p>"));
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    return true;
                }
                else
                    return false;
            }
        });

        makePopForceShowIcon(popupMenu);
        popupMenu.show();
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source,
                                         Intent intent) {
        Toast.makeText(this, intent.getComponent().toString(),
                Toast.LENGTH_LONG).show();

        return(false);
    }

    private void deleteEventId(long eventId) {

        if (eventId != -1) {

            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            Uri deleteUri = null;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, _eventId);
            int rows = getContentResolver().delete(deleteUri, null, null);

            if (rows > 0) {
                _eventId = -1;
                saveOrUpdateAfterReminderSet();
            }

            Toast.makeText(NewEditNoteActivity.this, R.string.delete + " " + rows + R.string.reminder + " ", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(NewEditNoteActivity.this, R.string.no_reminer_delete, Toast.LENGTH_SHORT).show();
    }

    private void confirmationDateDialog() {

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
        cdp.show(this.getSupportFragmentManager(), "Material Calendar Example");

        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int month, int day) {
                final int _year = year;
                final int _month = month;
                final int _day = day;

                try {
                    RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment();

                    rtpd.setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialTimePickerDialogFragment dialog, final int hourOfDay, final int minute) {

                            SparseArray<String> calendarNamesArray = queryCalendars();
                            List<String> calendarNamesList = createDialogForCalendarSelection(calendarNamesArray);

                            new MaterialDialog.Builder(NewEditNoteActivity.this)
                                    .title("Choose Calendar Name")
                                    .items(calendarNamesList)
                                    .itemsCallbackSingleChoice(1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            /**
                                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                             * returning false here won't allow the newly selected radio button to actually be selected.
                                             **/

                                            if (_eventId != -1)
                                            {
                                                deleteEventId(_eventId);
                                            }

                                            Calendar beginTime = Calendar.getInstance();
                                            beginTime.set(_year, _month, _day, hourOfDay, minute);
                                            long start = beginTime.getTimeInMillis();
                                            ContentValues values = new ContentValues();
                                            values.put(CalendarContract.Events.DTSTART, start);
                                            values.put(CalendarContract.Events.DTEND, start);
                                            values.put(CalendarContract.Events.TITLE, title.getText().toString());
                                            //values.put(CalendarContract.Events.EVENT_LOCATION, "Athens");
                                            TimeZone timeZone = TimeZone.getDefault();
                                            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                                            //values.put(CalendarContract.Events.EVENT_TIMEZONE, "Greece/Athens");
                                            values.put(CalendarContract.Events.CALENDAR_ID, which + 1);
                                            //values.put(CalendarContract.Events.ORGANIZER, "jkalimer13@gmail.com");
                                            values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString());
                                            // reasonable defaults exist:
                                            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
                                            values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,
                                                    CalendarContract.Events.STATUS_CONFIRMED);
                                            values.put(CalendarContract.Events.ALL_DAY, 0);
                                            values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
                                            values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 1);
                                            values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

                                            Uri uri =
                                                    getContentResolver().
                                                            insert(CalendarContract.Events.CONTENT_URI, values);
                                            long eventId = new Long(uri.getLastPathSegment());

                                            values.clear();
                                            values.put(CalendarContract.Reminders.EVENT_ID, eventId);
                                            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                                            values.put(CalendarContract.Reminders.MINUTES, Integer.parseInt(Utils.getValueFromListPreferenceReminderTime(_activity, "reminder_time")));
                                            getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);

                                            _eventId = eventId;

                                            if (_eventId != -1) {
                                                saveOrUpdateAfterReminderSet();
                                                Toast.makeText(NewEditNoteActivity.this, R.string.set_reminder_message + _day + "-" + _month + "-" + _year + " " + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
                                            }
                                            else
                                                Toast.makeText(NewEditNoteActivity.this, "R.string.set_reminder_problem", Toast.LENGTH_SHORT).show();

                                            return true;
                                        }
                                    })
                                    .positiveText(R.string.choice)
                                    .show();
                        }
                    });
                    rtpd.show(getSupportFragmentManager(), "time");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public SparseArray<String> queryCalendars() {

        Cursor cursor;

        if (android.os.Build.VERSION.SDK_INT <= 7) {
            cursor = getContentResolver().query(Uri.parse("content://calendar/calendars"), new String[] { "_id", "displayName" }, null, null, null);
        }
        else if (android.os.Build.VERSION.SDK_INT <= 14) {
            cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), new String[] { "_id", "displayName" }, null, null, null);
        }
        else {
            cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), new String[] { "_id", "calendar_displayName" }, null, null, null);
        }
        // Get calendars name
        SparseArray<String> calendarNamesArray = new SparseArray<String>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String[] calendarNames = new String[cursor.getCount()];
            // Get calendars id
            int calendarIds[] = new int[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                calendarIds[i] = cursor.getInt(0);
                calendarNames[i] = cursor.getString(1);
                calendarNamesArray.put(cursor.getInt(0), cursor.getString(1));
                cursor.moveToNext();
            }
        } else {
            //Log.e("No calendar found in the device");
        }

        return calendarNamesArray;
    }

    public List<String> createDialogForCalendarSelection(SparseArray<String> calendarNamesArray) {

        List<String> calendarNamesList = new ArrayList<String>(calendarNamesArray.size());
        for (int i = 0; i < calendarNamesArray.size(); i++)
            calendarNamesList.add(calendarNamesArray.valueAt(i));

        return calendarNamesList;
    }

    private void saveOrUpdateAfterReminderSet() {

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        noteObject = new noteObject();
        noteObject.setTitle(title.getText().toString());
        noteObject.setDescription(description.getText().toString());
        noteObject.setDate(date);
        noteObject.setColor(_colorCode);
        noteObject.setEventId(_eventId);
        noteObject.setCheck(_check);

        NotesDataSource dbAdapter = new NotesDataSource(NewEditNoteActivity.this);
        dbAdapter.open();
        long returnId;
        if (newNote) {
            returnId = dbAdapter.createNote(noteObject);
            if (returnId > 0) {
                _idNote = returnId;
                newNote = false;
                Toast.makeText(NewEditNoteActivity.this, R.string.note_saved, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            noteObject.setId(_idNote);
            returnId = dbAdapter.updateNote(noteObject);
            if (returnId > 0)
                Toast.makeText(NewEditNoteActivity.this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        }
        dbAdapter.close();

        Utils.updateWidget(this);
    }

    private void enableMenuItems(PopupMenu popupMenu) {
        MenuItem delete = popupMenu.getMenu().findItem(R.id.delete);
        MenuItem check = popupMenu.getMenu().findItem(R.id.check);
        MenuItem share = popupMenu.getMenu().findItem(R.id.share);

        if (newNote) {
            delete.setEnabled(false);
            delete.getIcon().setAlpha(130);

            share.setEnabled(false);
            share.getIcon().setAlpha(130);
        } else {
            delete.setEnabled(true);
            delete.getIcon().setAlpha(255);

            share.setEnabled(true);
            share.getIcon().setAlpha(255);
        }

        if (_check) {
            check.setIcon(R.drawable.ic_assignment_white_24dp);
            check.setTitle(R.string.unCheck);
        } else {
            check.setIcon(R.drawable.ic_assignment_turned_in_white_24dp);
            check.setTitle(R.string.check);

        }
    }

    private void makePopForceShowIcon(PopupMenu popupMenu) {
        try {
            Field mFieldPopup = popupMenu.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popupMenu);
            mPopup.setForceShowIcon(true);
        } catch (Exception e) {

        }
    }

    private void deleteNote() {

        Dialogs alert = new Dialogs(this, "", "The note will be delete.", "Delete", "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotesDataSource dbAdapter = new NotesDataSource(getApplicationContext());
                dbAdapter.open();
                if (_eventId != -1)
                    deleteEventId(_eventId);

                boolean deleteRow = dbAdapter.deleteNote(_idNote);
                if (!deleteRow)
                    Toast.makeText(getApplicationContext(), "The row couldn't delete due to a problem", Toast.LENGTH_LONG).show();
                dbAdapter.close();

                Intent intent = new Intent(NewEditNoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, null);

        alert.createAlertDialog();

        Utils.updateWidget(this);

    }

    private void saveNote() {

        if (floatingTitleLabel.isErrorEnabled() || floatingDescriptionLabel.isErrorEnabled())
        {
            Toast.makeText(NewEditNoteActivity.this, R.string.errors_save_note, Toast.LENGTH_LONG).show();
            return;
        }

        if (floatingTitleLabel.getEditText().length() == 0)
        {
            Toast.makeText(NewEditNoteActivity.this, R.string.errors_save_note_title, Toast.LENGTH_LONG).show();
            return;
        }

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        noteObject note = new noteObject();
        note.setTitle(title.getText().toString());
        note.setDescription(description.getText().toString());
        note.setDate(date);
        note.setColor(_colorCode);
        note.setEventId(_eventId);
        note.setCheck(_check);

        NotesDataSource dbAdapter = new NotesDataSource(this);
        dbAdapter.open();
        if (newNote)
            dbAdapter.createNote(note);
        else {
            note.setId(_idNote);
            dbAdapter.updateNote(note);
        }
        dbAdapter.close();

        Utils.updateWidget(this);

        Intent intent = new Intent(NewEditNoteActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void setAlphaAndCheckNote(float alpha) {

        title.setPaintFlags((alpha == 0.5f) ? title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : 0);
        title.setAlpha(alpha);
        description.setAlpha(alpha);
        if (!newNote) {
            ((TextView) toolbar.getChildAt(2)).setPaintFlags((alpha == 0.5f) ? title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : 0);
            ((TextView) toolbar.getChildAt(2)).setAlpha(alpha);
        }
    }
}
