package com.example.news_app.databases.bookmarksDb;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BookMark {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "bookMarkTheme")
    private String bookMarkTheme;

    public BookMark(int id, String bookMarkTheme) {
        this.id = id;
        this.bookMarkTheme = bookMarkTheme;
    }

    @Ignore
    public BookMark(String bookMarkTheme) {
        this.bookMarkTheme = bookMarkTheme;
    }

    public String getBookMarkTheme() {
        return bookMarkTheme;
    }

    public void setBookMarkTheme(String bookMarkTheme) {
        this.bookMarkTheme = bookMarkTheme;
    }

    static public ArrayList<String> getStringBookMarksList(List <BookMark> bookMarkList){
        ArrayList<String> outputList = new ArrayList<>();
        for (BookMark bookMark: bookMarkList){
            outputList.add(bookMark.bookMarkTheme);
        }
        return outputList;
    }

    static public List<BookMark> getFromStringBookMarksList(ArrayList <String> bookMarkList){
        ArrayList<BookMark> outputList = new ArrayList<>();
        for (String bookMark: bookMarkList){
            outputList.add(new BookMark(bookMark));
        }
        return outputList;
    }

    @NonNull
    @Override
    public String toString() {
        return bookMarkTheme;
    }
}
