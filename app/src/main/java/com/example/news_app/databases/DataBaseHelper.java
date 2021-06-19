package com.example.news_app.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.example.news_app.models.BookMark;
import com.example.news_app.models.History;
import com.example.news_app.models.CentBankCurrency;
import com.example.news_app.models.News;

import java.util.List;

// SingleTone class
public class DataBaseHelper {

    @SuppressLint("StaticFieldLeak")
    private static DataBaseHelper dataBaseHelper;
    private AppDataBase appDataBase;

    public static DataBaseHelper getDataBaseHelperInstance(Context context) {
        if (dataBaseHelper == null) {
            if (context != null) dataBaseHelper = new DataBaseHelper(context);
            else return null;
        }
        return dataBaseHelper;
    }

    private DataBaseHelper(Context context) {
        appDataBase = Room.databaseBuilder(context, AppDataBase.class, "dataBase.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetBookMarks extends AsyncTask<Void, Void, List<BookMark>> {

        private final OnGetBookMarksListener bookMarksListener;

        public GetBookMarks(OnGetBookMarksListener bookMarksListener) {
            this.bookMarksListener = bookMarksListener;
        }

        @Override
        protected List<BookMark> doInBackground(Void... voids) {
            return appDataBase.getBookmarkDao().getAllBookMarks();
        }

        @Override
        protected void onPostExecute(List<BookMark> bookMarks) {
            bookMarksListener.onGet(bookMarks);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetHotNews extends AsyncTask<Void, Void, List<News>> {

        private final OnGetHotNewsListener hotNewsListener;

        public GetHotNews(OnGetHotNewsListener hotNewsListener) {
            this.hotNewsListener = hotNewsListener;
        }

        @Override
        protected List<News> doInBackground(Void... voids) {
            return appDataBase.getHotNewsDao().getAllNews();
        }

        @Override
        protected void onPostExecute(List<News> list) {
            hotNewsListener.onGet(list);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetCurrencies extends AsyncTask<Void, Void, List<CentBankCurrency>>{

        private final OnGetCurrencyListener currencyListener;

        public GetCurrencies(OnGetCurrencyListener currencyListener) {
            this.currencyListener = currencyListener;
        }

        @Override
        protected List<CentBankCurrency> doInBackground(Void... voids) {
            return appDataBase.getCurrencyDao().getAllCurrencies();
        }

        @Override
        protected void onPostExecute(List<CentBankCurrency> centBankCurrencies) {
            currencyListener.onGet(centBankCurrencies);
        }
    }

    public interface OnGetBookMarksListener {
        void onGet(List<BookMark> listBookMarks);
    }

    public interface OnGetHotNewsListener {
        void onGet(List<News> listNews);
    }

    public interface OnGetCurrencyListener {
        void onGet(List<CentBankCurrency> listCurrency);
    }

    public interface OnGetHistoryListener {
        void onGet(List<History> listHistory);
    }
}
