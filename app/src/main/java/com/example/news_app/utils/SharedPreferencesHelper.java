package com.example.news_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.news_app.R;

public class SharedPreferencesHelper {

    static SharedPreferences sharedPreferences;

    public static void writeToPref(String prefKey, String textToWrite, Context context){
        sharedPreferences = context.getSharedPreferences(context.getResources()
                .getString(R.string.main_pref_dir), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(prefKey,textToWrite).apply();
    }

    public static String readFromPref(String prefKey, Context context){
        sharedPreferences = context.getSharedPreferences(context.getResources()
                .getString(R.string.main_pref_dir), Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefKey, null);
    }

}
