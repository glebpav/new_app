package com.example.news_app.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {

    private static final String TAG = "USER_CLASS_SPACE";

    // information from python db
    private int id;
    private String name;
    private String login;
    private String password;
    private String history;
    private String themes;
    private String sites;

    // data for mobile use
    private ArrayList<String> listHistory;
    private ArrayList<String> listThemes;

    public User(int id, String name, String login, String password, String history, String themes, String sites) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.history = history;
        this.themes = themes;
        this.sites = sites;
    }

    public User() {
    }

    public String getSites() {
        return sites;
    }

    public void setSites(String sites) {
        this.sites = sites;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public ArrayList<String> getListHistory() {
        return listHistory;
    }

    public ArrayList<String> getListThemes() {
        return listThemes;
    }


    public void fillListThemes() {
        Log.d(TAG, "user themes : " + themes);
        Log.d(TAG, String.valueOf(themes.split(";").length));
        listThemes = new ArrayList<>();
        if (!themes.contains(";") && themes.length() != 0) listThemes.add(themes);
        for (String str : themes.split(";")) {
            Log.d(TAG, str);
            if (str.length() != 0 && !listThemes.contains(str))
                listThemes.add(str);
        }
    }

    public void fillListHistory() {
        Log.d(TAG, "user history : " + history);
        Log.d(TAG, String.valueOf(history.split(";").length));
        listHistory = new ArrayList<>();
        if (!history.contains(";") && history.length() != 0) listHistory.add(history);
        for (String str : history.split(";")) {
            Log.d(TAG, str);
            if (str.length() != 0)
                listHistory.add(str);
        }
    }

    public void clearThemes() {
        boolean isCleared;
        do {
            isCleared = false;
            if (themes.isEmpty()) return;
            if (themes.contains(";;")) {
                themes = themes.replace(";;", ";");
                isCleared = true;
            }
            if (themes.charAt(0) == ';') {
                themes = themes.substring(1);
                isCleared = true;
            }
        } while (isCleared);
    }

    static public User serializeUser(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            return new User(Integer.parseInt(obj.getString("id")),
                    obj.getString("name"),
                    obj.getString("login"),
                    obj.getString("password"),
                    obj.getString("history"),
                    obj.getString("themes"),
                    obj.getString("sites"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
