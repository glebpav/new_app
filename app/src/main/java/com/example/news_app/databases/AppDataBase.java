package com.example.news_app.databases;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.news_app.databases.bookmarksDb.BookMark;
import com.example.news_app.databases.bookmarksDb.BookMarkDao;
import com.example.news_app.databases.currencyDb.CurrencyDao;
import com.example.news_app.databases.historyDb.History;
import com.example.news_app.databases.historyDb.HistoryDao;
import com.example.news_app.models.CentBankCurrency;

import java.util.Currency;

@Database(entities = {History.class, CentBankCurrency.class, BookMark.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract HistoryDao getHistoryDao();
    public abstract CurrencyDao getCurrencyDao();
    public abstract BookMarkDao getBookmarkDao();
}
