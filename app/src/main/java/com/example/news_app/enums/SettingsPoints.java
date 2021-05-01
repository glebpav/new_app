package com.example.news_app.enums;


import com.example.news_app.R;

public enum SettingsPoints {
    CHANGE_NAME(R.drawable.ic_baseline_drive_file_rename_outline_24, R.string.rename),
    SHOW_HISTORY(R.drawable.ic_baseline_history_24, R.string.history),
    CHANGE_SOURCES(R.drawable.ic_baseline_find_replace_24, R.string.change_sources),
    LOG_OUT(R.drawable.ic_baseline_logout_24, R.string.log_out);

    private final int idIc;
    private final int idUnderText;

    SettingsPoints(int idIc, int idUnderText) {
        this.idIc = idIc;
        this.idUnderText = idUnderText;
    }

    public int getIdIc() {
        return idIc;
    }

    public int getIdUnderText() {
        return idUnderText;
    }
}
