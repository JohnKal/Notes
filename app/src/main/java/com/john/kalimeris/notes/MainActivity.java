package com.john.kalimeris.notes;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.heinrichreimersoftware.androidissuereporter.IssueReporterLauncher;
import com.webianks.easy_feedback.EasyFeedback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.everything.providers.android.calendar.CalendarProvider;
import me.everything.providers.android.calendar.Event;

import static com.john.kalimeris.notes.R.id.calendar;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    public static RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private String[] myDataset;
    private List<noteObject> _myDataset;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    Paint p = new Paint();
    SearchView searchView;
    boolean newObject = false;
    int count = 0;
    private LinearLayout sortLayout;
    private ImageView imagViewSort;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        checkForAlarms();
        setAlarmCounts(navigationView);

        sortLayout = (LinearLayout) findViewById(R.id.linearLayoutSort);
        imagViewSort = (ImageView) findViewById(R.id.imageViewSort);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewEditNoteActivity.class);
                intent.putExtra("newNote", true);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        buildDatasetWithObjects();
        checkIfListHasNotes();

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(_myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        initSwipe();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        sortLayout.setOnTouchListener(new View.OnTouchListener() {
            Comparator<noteObject> comparatorForSort;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sortLayout.setBackgroundColor(getResources().getColor(R.color.yellowPastel));
                    // Do what you want
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    sortLayout.setBackgroundColor(getResources().getColor(R.color.bpTransparent));

                    new MaterialDialog.Builder(MainActivity.this)
                            .title(R.string.sort_based_on)
                            .items(R.array.sort_notes)
                            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                    String[] sortNotesOptions = getResources().getStringArray(R.array.sort_notes);

                                    if (sortNotesOptions[which].equalsIgnoreCase(getResources().getString(R.string.sort_title))) {
                                        Collections.sort(_myDataset, noteObject.NotesComparatorTitle);
                                        mAdapter.setFilter(_myDataset);
                                    } else if (sortNotesOptions[which].equalsIgnoreCase(getResources().getString(R.string.sort_description))) {
                                        Collections.sort(_myDataset, noteObject.NotesComparatorDescription);
                                        mAdapter.setFilter(_myDataset);
                                    } else if (sortNotesOptions[which].equalsIgnoreCase(getResources().getString(R.string.sort_date))) {
                                        Collections.sort(_myDataset, noteObject.NotesComparatorDate);
                                        mAdapter.setFilter(_myDataset);
                                    } else if (sortNotesOptions[which].equalsIgnoreCase(getResources().getString(R.string.sort_color))) {
                                        Collections.sort(_myDataset, noteObject.NotesComparatorColor);
                                        mAdapter.setFilter(_myDataset);
                                    } else if (sortNotesOptions[which].equalsIgnoreCase(getResources().getString(R.string.sort_check))) {
                                        Collections.sort(_myDataset, noteObject.NotesComparatorCheck);
                                        mAdapter.setFilter(_myDataset);
                                    }

                                    return true;
                                }
                            })
                            .positiveText(R.string.choice)
                            .show();
                }
                return false;
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Utils.setFullScreen(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkIfListHasNotes() {

        if (_myDataset.size() == 0) {
            ((TextView) findViewById(R.id.text_zero_notes)).setVisibility(View.VISIBLE);
            sortLayout.setVisibility(View.GONE);
        }
        else {
            ((TextView) findViewById(R.id.text_zero_notes)).setVisibility(View.GONE);
            sortLayout.setVisibility(View.VISIBLE);
        }
    }

    private void checkForAlarms() {

        NotesDataSource dbAdapter = new NotesDataSource(MainActivity.this);
        dbAdapter.open();
        List<noteObject> notes = dbAdapter.getAllNotesWithEvents();
        dbAdapter.close();

        for (noteObject note : notes) {

            String[] args = {String.valueOf(note.getEventId())};

            CalendarProvider calendarProvider = new CalendarProvider(MainActivity.this);
            Event event = calendarProvider.getEvent(note.getEventId());
            if (event == null) {
                note.setEventId((long) -1);
                NotesDataSource dbAdapter2 = new NotesDataSource(MainActivity.this);
                dbAdapter2.open();
                dbAdapter2.updateNote(note);
                dbAdapter2.close();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        checkForAlarms();
        setAlarmCounts(navigationView);
        mAdapter.setFilter(buildDatasetWithObjects());
    }

    public void initNavigationDrawer() {

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View hView =  navigationView.getHeaderView(0);
        ImageView imageView = (ImageView)hView.findViewById(R.id.img_header_bg);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case calendar:
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("content://com.android.calendar/time"));
                        startActivity(i);
                        /*Intent i = new Intent();
                        ComponentName cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
                        i.setComponent(cn);
                        startActivity(i);*/
                        /*if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                                // Explain to the user why we need to read the contacts
                            }

                            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 1);
                        }*/
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.backup:
                        backupNotes();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.restore:

                        final Handler handler = new Handler();

                        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
                        progress.setTitle(R.string.loading);
                        progress.setMessage(getResources().getString(R.string.restoring_notes));
                        progress.setCancelable(false);
                        progress.show();

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    restoreNotes();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            progress.dismiss();
                                            final List<noteObject> restoreNotesList = buildDatasetWithObjects();
                                            mAdapter.setFilter(restoreNotesList);

                                            checkIfListHasNotes();
                                            Toast.makeText(MainActivity.this, R.string.restore_completed, Toast.LENGTH_LONG).show();

                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        drawerLayout.closeDrawers();
                        break;
                    case R.id.trash:
                        new MaterialDialog.Builder(MainActivity.this)
                                .title(R.string.delete_notes)
                                .content(R.string.delete_notes_message)
                                .positiveText(R.string.delete_Cap)
                                .negativeText(R.string.cancel_cap)
                                .iconRes(R.drawable.ic_information_variant_grey600_24dp)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        // TODO
                                        NotesDataSource dbAdapter = new NotesDataSource(MainActivity.this);
                                        dbAdapter.open();
                                        dbAdapter.deleteAllNotes();
                                        dbAdapter.close();

                                        buildDatasetWithObjects();
                                        mAdapter.setFilter(_myDataset);
                                        checkIfListHasNotes();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                })
                                .show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Intent intent = new Intent(MainActivity.this, com.john.kalimeris.notes.Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.feedback:
                        new EasyFeedback.Builder(MainActivity.this)
                                .withEmail("jkalimer13@gmail.com")
                                .withSystemInfo()
                                .build()
                                .start();
                        break;
                    case R.id.reportIssue:
                        if (Utils.isOnline(MainActivity.this)) {
                            IssueReporterLauncher.forTarget("JohnKal", "Notes")
                                    // [Recommended] Theme to use for the reporter
                                    // (See #theming for further information.)
                                    .theme(R.style.Theme_App_Light)
                                    // [Optional] Auth token to open issues if users don't have a GitHub account
                                    // You can register a bot account on GitHub and copy ist OAuth2 token here.
                                    // (See #how-to-create-a-bot-key for further information.)
                                    .guestToken("")
                                    // [Optional] Force users to enter an email adress when the report is sent using
                                    // the guest token.
                                    .guestEmailRequired(true)
                                    // [Optional] Set a minimum character limit for the description to filter out
                                    // empty reports.
                                    .minDescriptionLength(20)
                                    // [Optional] Disable back arrow in toolbar
                                    .homeAsUpEnabled(true)
                                    .launch(MainActivity.this);
                        }
                        else {
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title(R.string.device_offline)
                                    .content(R.string.connect_to_internet)
                                    .positiveText(R.string.ok)
                                    .show();
                        }
                        break;
                    case R.id.shareSocial:

                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        String url = "";
                        try {
                            url = "market://details?id=" + appPackageName;
                        } catch (android.content.ActivityNotFoundException anfe) {
                            url = "https://play.google.com/store/apps/details?id=" + appPackageName;
                        }

                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);

                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Check out this app! " + url);
                        startActivity(Intent.createChooser(intent, "Share"));
                        break;
                    case R.id.rateApp:
                        new MaterialDialog.Builder(MainActivity.this)
                                .title(R.string.rate_dialog_title)
                                .content(R.string.rate_dialog_content)
                                .positiveText(R.string.rate_now)
                                .negativeText(R.string.rate_later)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        // TODO
                                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        // To count with Play market backstack, After pressing back button,
                                        // to taken back to our application, we need to add following flags to intent.
                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(goToMarket);
                                        } catch (ActivityNotFoundException ex) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                        }
                                    }
                                })
                                .show();
                        break;
                }
                return true;
            }
        });
        //View header = navigationView.getHeaderView(0);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                //super.onDrawerOpened(v);
                setAlarmCounts(navigationView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        final int REQUEST_PERMISSIONS = 1;

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Call whatever you want
                    //queryCalendars();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    private void setAlarmCounts(NavigationView navigationView) {
        NotesDataSource dbAdapter = new NotesDataSource(this);
        dbAdapter.open();
        int numberAlarms = dbAdapter.countAlarms();
        dbAdapter.close();

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(calendar);
        LinearLayout calendarReminderView = (LinearLayout) MenuItemCompat.getActionView(menuItem);

        TextView reminderText = (TextView) calendarReminderView.findViewById(R.id.remindersText);

        reminderText.setText(String.valueOf(numberAlarms));
    }

    private void restoreNotes() throws IOException, JSONException {

        deleteAllNotes();

        File sd = new File(Environment.getExternalStorageDirectory() + getResources().getString(R.string.dIR_PARENT));
        File backup = new File(sd, "notes.json");
        if (!backup.exists()) {
            //Toast.makeText(this, "File for restore notes not found.", Toast.LENGTH_LONG);
            return;
        }

        InputStream is = new FileInputStream(backup);
        int size = is.available();
        byte[] buffer = new byte[size];

        is.read(buffer);
        is.close();

        String json = new String(buffer, "UTF-8");
        JSONObject jsonNotes = new JSONObject(json);

        List<noteObject> notesForInsert = parseJsonObject(jsonNotes);

        NotesDataSource dbAdapter = new NotesDataSource(this);
        dbAdapter.open();
        dbAdapter.insertMultipleNotes(notesForInsert);
        dbAdapter.close();
    }

    private List<noteObject> parseJsonObject(JSONObject jsonNotes) throws JSONException {

        List<noteObject> allNotes = new ArrayList<noteObject>();

        JSONArray cast = jsonNotes.getJSONArray("notes");
        for (int i = 0; i < cast.length(); i++) {
            JSONObject jsonNote = cast.getJSONObject(i);

            noteObject note = new noteObject();
            note.setTitle(jsonNote.getString("title"));
            note.setDescription(jsonNote.getString("description"));
            note.setDate(jsonNote.getString("date"));
            note.setColor(jsonNote.getString("color"));
            note.setEventId((long) -1);
            note.setCheck(Boolean.parseBoolean(jsonNote.getString("check")));
            allNotes.add(note);
        }

        return allNotes;
    }

    private void deleteAllNotes() {
        NotesDataSource dbAdapter = new NotesDataSource(this);
        dbAdapter.open();
        boolean deletedRows = dbAdapter.deleteAllNotes();
        dbAdapter.close();

        /*if (deletedRows) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(this, "Notes Deleted.", Toast.LENGTH_LONG).show();
                }
            });
        }*/

    }

    private void backupNotes() {

        String fileNotes = null;
        try {
            fileNotes = createJsonString().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveData(fileNotes);

        File sd = new File(Environment.getExternalStorageDirectory() + getResources().getString(R.string.dIR_PARENT));
        try {
            File backup = new File(sd, "notes.json");
            Toast.makeText(this, R.string.backup_completed, Toast.LENGTH_LONG).show();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Toast.makeText(this, R.string.backup_failed, Toast.LENGTH_LONG).show();
        }
    }

    private JSONObject createJsonString() throws JSONException {
        List<noteObject> notes = new ArrayList<noteObject>();
        NotesDataSource dbAdapter = new NotesDataSource(this);
        dbAdapter.open();
        notes = dbAdapter.getAllNotes();
        dbAdapter.close();

        JSONObject jsonNotes = new JSONObject();
        JSONArray jsonArrayNotes = new JSONArray();
        JSONObject jsonNoteDetails = new JSONObject();

        for (noteObject note : notes) {

            jsonNoteDetails.put("title", note.getTitle());
            jsonNoteDetails.put("description", note.getDescription());
            jsonNoteDetails.put("date", note.getDate());
            jsonNoteDetails.put("color", note.getColor());
            jsonNoteDetails.put("eventId", -1);
            jsonNoteDetails.put("check", note.getCheck());

            jsonArrayNotes.put(jsonNoteDetails);

            jsonNoteDetails = new JSONObject();
            //clearJsonObject(jsonNoteDetails);
        }

        jsonNotes.put("notes", jsonArrayNotes);

        return jsonNotes;
    }

    public void saveData(String mJsonNotes) {
        try {
            if (!isExternalStorageWritable())
                return;
            File notesFolder = new File(Environment.getExternalStorageDirectory() + getResources().getString(R.string.dIR_PARENT));
            if (!notesFolder.exists()) {
                notesFolder.mkdirs();
            }

            File backup = new File(notesFolder, "notes.json");
            if (backup.exists()) backup.delete();

            FileWriter fileWriter = new FileWriter(backup.getAbsolutePath());
            fileWriter.write(mJsonNotes);
            fileWriter.flush();
            fileWriter.close();
            return;
        } catch (IOException e) {
            Toast.makeText(this, R.string.backup_write_fail, Toast.LENGTH_LONG).show();
            return;
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private List<noteObject> buildDatasetWithObjects() {

        _myDataset = new ArrayList<noteObject>();

        NotesDataSource dbAdapter = new NotesDataSource(this);
        dbAdapter.open();
        _myDataset = dbAdapter.getAllNotes();
        dbAdapter.close();

        return _myDataset;
    }

    private void initSwipe() {
        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {

                    long _idDelete = _myDataset.get(position).getId();

                    mAdapter.removeItem(position);

                    NotesDataSource dbAdapter = new NotesDataSource(getApplicationContext());
                    dbAdapter.open();
                    boolean deleteRow = dbAdapter.deleteNote(_idDelete);
                    if (!deleteRow)
                        Toast.makeText(getApplicationContext(), R.string.delete_fail_message, Toast.LENGTH_LONG).show();
                    dbAdapter.close();

                    buildDatasetWithObjects();
                    checkIfListHasNotes();
                    mAdapter.notifyDataSetChanged();

                    Utils.updateWidget(MainActivity.this);

                } else {
                    noteObject note = _myDataset.get(position);

                    Intent intent = new Intent(MainActivity.this, NewEditNoteActivity.class);
                    intent.putExtra("noteObject", note);
                    intent.putExtra("newNote", false);
                    startActivity(intent);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_create_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        //searchItem.expandActionView();
        //searchView.requestFocus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        /*NotesDataSource dbAdapter = new NotesDataSource(getApplicationContext());
        dbAdapter.open();

        Cursor cursor = dbAdapter.cursorSuggestionList(newText, newText);

        searchView.setSuggestionsAdapter(new SuggestionsCursorAdapter(getApplicationContext(), cursor));*/

        //dbAdapter.close();

        final List<noteObject> filteredNotesList = filter(_myDataset, newText);
        mAdapter.setFilter(filteredNotesList);

        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return false;
    }


    private List<noteObject> filter(List<noteObject> newNotes, String query) {
        query = query.toLowerCase();

        final List<noteObject> filteredModelList = new ArrayList<>();

        for (noteObject newNote : newNotes) {
            final String title = newNote.getTitle().toLowerCase();
            final String description = newNote.getDescription().toLowerCase();

            if (title.contains(query) || description.contains(query)) {
                filteredModelList.add(newNote);
            }
        }
        return filteredModelList;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}