package com.john.kalimeris.notes;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

/**
 * Created by JohnK on 16/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override public void onDisplayPreferenceDialog(Preference preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
