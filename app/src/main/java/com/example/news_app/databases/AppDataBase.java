package com.example.news_app.databases;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.news_app.databases.daos.BookMarkDao;
import com.example.news_app.databases.daos.CurrencyDao;
import com.example.news_app.databases.daos.HistoryDao;
import com.example.news_app.databases.daos.HotNewsDao;
import com.example.news_app.models.BookMark;
import com.example.news_app.models.History;
import com.example.news_app.models.CentBankCurrency;
import com.example.news_app.models.News;

@Database(entities = {History.class, CentBankCurrency.class, BookMark.class, News.class}, version = 2)
public abstract class AppDataBase extends RoomDatabase {
    public abstract HistoryDao getHistoryDao();
    public abstract CurrencyDao getCurrencyDao();
    public abstract BookMarkDao getBookmarkDao();
    public abstract HotNewsDao getHotNewsDao();
}
