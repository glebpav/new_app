package com.example.news_app.models;

import android.util.Log;

import java.util.ArrayList;

public class SavedData {

    private static final String TAG = "SAVING_DATA_CLASS_SPACE";

    private int id;
    private String name;
    private String login;
    private String password;

    private ArrayList<News> listTopNews;
    private ArrayList<String> listSites;
    private ArrayList<String> listHistory;
    private ArrayList<String> listThemes;
    private ArrayList<String> listSelectedCurrency;
    private ArrayList<CentBankCurrency> listAllCurrency;

    public SavedData() {
        listAllCurrency = new ArrayList<>();
        listTopNews = new ArrayList<>();
        listHistory = new ArrayList<>();
        listSelectedCurrency = new ArrayList<>();
        listSites = new ArrayList<>();
        listThemes = new ArrayList<>();
    }

    public SavedData(User mUser) {
        id = mUser.getId();
        name = mUser.getName();
        login = mUser.getLogin();
        password = mUser.getPassword();

        listSites = mUser.fillListSites();
        listThemes = mUser.fillListThemes();
        listHistory = mUser.fillListHistory();
        listSelectedCurrency = mUser.fillListCurrency();

        listAllCurrency = new ArrayList<>();
        listTopNews = new ArrayList<>();
    }

    public void prepareToSave(User user, ArrayList<CentBankCurrency> listCurrency) {
        id = user.getId();
        name = user.getName();
        login = user.getLogin();
        password = user.getPassword();

        listSites = user.fillListSites();
        listThemes = user.fillListThemes();
        listHistory = user.fillListHistory();
        listSelectedCurrency = user.fillListCurrency();

        listAllCurrency = listCurrency;
    }

    public boolean equals(SavedData obj) {

        if (obj == null
                || obj.getListAllCurrency() == null
                || obj.getListSelectedCurrency() == null
                || listAllCurrency == null) return false;

        Log.d(TAG, "equals: " + obj.getListAllCurrency());

        if (!name.equals(obj.name)) return false;
        if (!listSites.equals(obj.listSites)) return false;
        if (!listThemes.equals(obj.listThemes)) return false;
        if (!listHistory.equals(obj.listHistory)) return false;
        if (listAllCurrency.size() != obj.listAllCurrency.size()) return false;
        for (int i = 0; i < listAllCurrency.size(); i++) {
            if (obj.listAllCurrency.get(i).getValue() != listAllCurrency.get(i).getValue())
                return false;
        }
        if (!listSelectedCurrency.equals(obj.listSelectedCurrency)) return false;

        Log.d(TAG, "equals: equals");
        return true;
    }

    public User getUser() {
        User user = new User(id, name, login, password, listHistory, listThemes, listSelectedCurrency, listSites);
        user.setThemes(arrayListToStr(listThemes));
        user.setCurrency(arrayListToStr(listSelectedCurrency));
        user.setSites(arrayListToStr(listSites));
        user.setHistory(arrayListToStr(listHistory));
        return user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<String> getListSites() {
        return listSites;
    }

    public void setListSites(ArrayList<String> listSites) {
        this.listSites = listSites;
    }

    public ArrayList<String> getListHistory() {
        return listHistory;
    }

    public void setListHistory(ArrayList<String> listHistory) {
        this.listHistory = listHistory;
    }

    public ArrayList<String> getListThemes() {
        return listThemes;
    }

    public void setListThemes(ArrayList<String> listThemes) {
        this.listThemes = listThemes;
    }

    public ArrayList<String> getListSelectedCurrency() {
        return listSelectedCurrency;
    }

    public void setListSelectedCurrency(ArrayList<String> listSelectedCurrency) {
        this.listSelectedCurrency = listSelectedCurrency;
    }

    public ArrayList<CentBankCurrency> getListAllCurrency() {
        return listAllCurrency;
    }

    public void setListAllCurrency(ArrayList<CentBankCurrency> listAllCurrency) {
        this.listAllCurrency = listAllCurrency;
    }

    public ArrayList<News> getListTopNews() {
        if (listTopNews == null)listTopNews = new ArrayList<>();
        return listTopNews;
    }

    public String arrayListToStr(ArrayList<String> list) {
        String output = "";
        for (String str : list) {
            str += str + ";";
        }
        return output;
    }

    public void setListTopNews(ArrayList<News> listTopNews) {
        if (listTopNews == null)listTopNews = new ArrayList<>();
        this.listTopNews = listTopNews;
    }
}
