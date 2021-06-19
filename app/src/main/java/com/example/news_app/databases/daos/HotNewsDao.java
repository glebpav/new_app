package com.example.news_app.databases.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.news_app.models.History;
import com.example.news_app.models.News;

import java.util.List;

@Dao
public interface HotNewsDao {

    @Insert
    void insertAll(News... news);

    @Query("DELETE FROM hotNews")
    void deleteAll();

    @Query("SELECT * FROM hotNews")
    List<News> getAllNews();

}
