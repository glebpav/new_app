package com.example.news_app.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.example.news_app.databases.bookmarksDb.BookMark;
import com.example.news_app.databases.historyDb.History;
import com.example.news_app.models.CentBankCurrency;

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
        appDataBase = Room.databaseBuilder(context, AppDataBase.class, "dataBase.db").build();
    }

    public AppDataBase getAppDataBase() {
        return appDataBase;
    }

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

    public interface OnGetBookMarksListener {
        void onGet(List<BookMark> listBookMarks);
    }

    public interface OnGetCurrencyListener {
        void onGet(List<CentBankCurrency> listCurrency);
    }

    public interface OnGetHistoryListener {
        void onGet(List<History> listHistory);
    }
}
