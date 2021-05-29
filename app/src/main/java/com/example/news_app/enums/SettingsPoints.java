package com.example.news_app.enums;


import com.example.news_app.R;

public enum SettingsPoints {
    CHANGE_NAME(R.drawable.ic_baseline_drive_file_rename_outline_24, R.string.rename),
    SHOW_HISTORY(R.drawable.ic_baseline_history_24, R.string.history),
    IN_FUTURE1(R.drawable.ic_baseline_attach_money_24, R.string.currency),
    IN_FUTURE2(R.drawable.ic_baseline_do_not_disturb_24, R.string.in_future),
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
