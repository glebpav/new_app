package com.example.news_app.models;

public class SettingTile {

    private int icId;
    private String underText;

    public SettingTile(int icId, String underText) {
        this.icId = icId;
        this.underText = underText;
    }

    public int getIcId() {
        return icId;
    }

    public void setIcId(int icId) {
        this.icId = icId;
    }

    public String getUnderText() {
        return underText;
    }

    public void setUnderText(String underText) {
        this.underText = underText;
    }
}
