package com.example.news_app.databases.historyDb;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.news_app.models.CentBankCurrency;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    void insertAll(History... histories);

    @Query("DELETE FROM history")
    void deleteAll();

    @Query("SELECT * FROM history")
    List<History> getAllCurrencies();

}
