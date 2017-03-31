package com.john.kalimeris.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by JohnK on 16/03/2017.
 */

public class Settings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDelegate().getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_ivy)));

        Utils.setFullScreen(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        /*String languageToLoad = Utils.getValueFromListPreferenceReminderTime(this, "language");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getBaseContext().getResources().updateConfiguration(config, null);*/

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        super.onBackPressed();
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
    }
}
