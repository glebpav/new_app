package com.example.news_app.databases.bookmarksDb;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookMarkDao {

    @Insert
    void insertAll(BookMark... bookMarks);

    @Delete
    void delete(BookMark bookMark);

    @Query("DELETE FROM bookmark")
    void deleteAll();

    @Query("SELECT * FROM bookmark")
    List<BookMark> getAllBookMarks();

}
