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

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JsonManager jsonManager;
        SavedData savedData;
        DataBaseHelper.getDataBaseHelperInstance(this);

        jsonManager = new JsonManager(this);
        savedData = jsonManager.readUserFromJson();
        if (!MakeRequests.isInternetAvailable(this) && savedData != null && savedData.getUser() != null) {
            gotoNextActivity(savedData.getUser());
        }
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.MA, new FragmentSignIn()).commit();
    }

    public void gotoNextActivity(User user) {
        Intent intent = new Intent(this, ActivityNews.class);
        intent.putExtra("id", user.getId());
        intent.putExtra("name", user.getName());
        intent.putExtra("login", user.getLogin());
        intent.putExtra("password", user.getLogin());
        intent.putExtra("history", user.getHistory());
        intent.putExtra("themes", user.getThemes());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("sites", user.getSites());
        intent.putExtra("currency", user.getCurrency());
        startActivity(intent);
    }


}