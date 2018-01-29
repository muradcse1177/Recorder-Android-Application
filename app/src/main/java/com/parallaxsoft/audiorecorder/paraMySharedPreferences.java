package com.parallaxsoft.audiorecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class paraMySharedPreferences {
    private static String makeHighQuality = "pref_high_quality";

    public static void paraSetPrefHighQuality(Context context, boolean isEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(makeHighQuality, isEnabled);
        editor.apply();
    }

    public static boolean paraGetPrefHighQuality(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(makeHighQuality, false);
    }
}
