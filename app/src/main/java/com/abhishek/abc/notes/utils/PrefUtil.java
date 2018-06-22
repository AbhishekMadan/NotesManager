package com.abhishek.abc.notes.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {

    private static final String APP_PREF = "notes_pref";
    public PrefUtil() {

    }

    private static SharedPreferences getApplicationSharedPref(Context context) {
        return context.getApplicationContext().getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
    }

    public static void saveAPIKey(Context context, String key) {
        SharedPreferences.Editor editor = getApplicationSharedPref(context).edit();
        editor.putString("API_KEY",key);
        editor.commit();
    }

    public static String getAPIKey(Context context) {
        return getApplicationSharedPref(context).getString("API_KEY",null);
    }
}
