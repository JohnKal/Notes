package com.john.kalimeris.notes;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import java.util.Set;

/**
 * Created by John on 13/11/16.
 */

public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static boolean getPreferenceBooleanValue(Context context, String preferenceValue) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(preferenceValue, true);
    }

    public static String getValueFromMultiselectCheckBox(Context context, String key, String entryValue) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> selections = sharedPrefs.getStringSet(key, null);
        if (selections == null || selections.size() == 0)
            return "false";
        if (selections.contains(entryValue))
            return "true";

        return "false";
    }

    public static String getValueFromListPreference(Context context, String key) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String listSelection = sharedPrefs.getString(key, "");
        if (listSelection.equalsIgnoreCase("small"))
            return "small";
        else if (listSelection.equalsIgnoreCase("medium"))
            return "medium";
        else if (listSelection.equalsIgnoreCase("large"))
            return "large";
        else
            return "";
    }

    public static String getValueFromListPreferenceReminderTime(Context context, String key) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String listSelection = sharedPrefs.getString(key, "");
        return listSelection;
    }

    public static boolean getValueFromCheckBoxPreference(Context context, String key) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean fullScreen = sharedPrefs.getBoolean(key, true);
        return fullScreen;
    }

    /**
     * Update Widget
     * @param packageContext
     */
    public static void updateWidget(Context packageContext) {

        Activity activity = (Activity) packageContext;

        Intent intent = new Intent(packageContext, NotesWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), NotesWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        activity.sendBroadcast(intent);
    }

    /**
     * Set intent for MainActivity after widget's click.
     * @param context
     */
    public static void intentMainActivity(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void intentNewNoteActivity(Context context, noteObject note) {

        Intent intent = new Intent(context, NewEditNoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("noteObject", note);
        intent.putExtra("newNote", false);
        context.startActivity(intent);
    }

    public static void setFullScreen(Activity activity) {

        if (Utils.getValueFromCheckBoxPreference(activity, "fullscreen")) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }
}
