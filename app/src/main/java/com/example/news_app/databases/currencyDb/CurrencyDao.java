package com.example.news_app.databases.currencyDb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.news_app.models.CentBankCurrency;

import java.util.List;

@Dao
public interface CurrencyDao {

    @Insert
    void insertAll(CentBankCurrency... currencies);

    @Delete
    void delete(CentBankCurrency currency);

    @Query("DELETE FROM centbankcurrency")
    void deleteAll();

    @Query("SELECT * FROM centbankcurrency")
    List<CentBankCurrency> getAllCurrencies();

}
