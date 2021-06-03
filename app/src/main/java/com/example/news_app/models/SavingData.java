package com.example.news_app.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SavingData {

    private static final String TAG = "SAVING_DATA_CLASS_SPACE";

    private int id;
    private String name;
    private String login;
    private String password;

    private ArrayList<String> listSites;
    private ArrayList<String> listHistory;
    private ArrayList<String> listThemes;
    private ArrayList<String> listSelectedCurrency;
    private ArrayList<CentBankCurrency> listAllCurrency;

    void prepareToSave(User user, ArrayList<CentBankCurrency> listCurrency) {
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

    public boolean equals(SavingData obj) {
        if (!name.equals(obj.name))return false;
        if (!listSites.equals(obj.listSites))return false;
        if (!listThemes.equals(obj.listThemes))return false;
        if (!listHistory.equals(obj.listHistory))return false;
        if (!listAllCurrency.equals(obj.listAllCurrency))return false;
        if (!listSelectedCurrency.equals(obj.listSelectedCurrency))return false;

        return true;
    }
}
