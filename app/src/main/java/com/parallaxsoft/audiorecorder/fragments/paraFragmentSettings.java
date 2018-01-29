package com.parallaxsoft.audiorecorder.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import com.parallaxsoft.audiorecorder.BuildConfig;
import com.parallaxsoft.audiorecorder.paraMySharedPreferences;
import com.parallaxsoft.audiorecorder.R;
import com.parallaxsoft.audiorecorder.activities.SettingsActivity;

public class paraFragmentSettings extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference paraHighQualityPref = (CheckBoxPreference) findPreference(getResources().getString(R.string.PHQK));
        paraHighQualityPref.setChecked(paraMySharedPreferences.paraGetPrefHighQuality(getActivity()));
        paraHighQualityPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                paraMySharedPreferences.paraSetPrefHighQuality(getActivity(), (boolean) newValue);
                return true;
            }
        });

        Preference paraAboutPref = findPreference(getString(R.string.pref_about_key));
        paraAboutPref.setSummary(getString(R.string.hjfghtrtrtutyirt, BuildConfig.VERSION_NAME));
        paraAboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                paraTLinFra paraparaTLinFra = new paraTLinFra();
                paraparaTLinFra.show(((SettingsActivity)getActivity()).getSupportFragmentManager().beginTransaction(), "dialog_licenses");
                return true;
            }
        });
    }
}
