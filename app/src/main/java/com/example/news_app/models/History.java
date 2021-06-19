package com.example.news_app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "History")
public class History {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "theme")
    private String theme;

    public History(int id, String theme) {
        this.id = id;
        this.theme = theme;
    }

    @Ignore
    public History(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

}
