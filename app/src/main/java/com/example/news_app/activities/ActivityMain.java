package com.example.news_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.news_app.R;
import com.example.news_app.databases.DataBaseHelper;
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.usualFragments.FragmentSignIn;
import com.example.news_app.models.SavedData;
import com.example.news_app.models.User;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.network.ParseWeather;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBaseHelper.getDataBaseHelperInstance(this);
        new ParseWeather(this);

        getSupportFragmentManager().beginTransaction().add(R.id.MA, new FragmentSignIn()).commit();
    }
}