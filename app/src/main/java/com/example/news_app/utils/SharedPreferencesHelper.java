package com.example.news_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    static SharedPreferences sharedPreferences;

    public static void writeToPref(String prefName, String prefKey, String textToWrite, Context context){
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(prefKey,textToWrite).apply();
    }

    public static String readFromPref(String prefName, String prefKey, Context context){
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefKey, null);
    }

}
