package com.example.news_app.enums;

import com.example.news_app.R;

public enum Sources {

    RBK(R.drawable.rbk, R.string.rbk, R.string.rbk_domen),
    LENTA(R.drawable.lenta, R.string.lenta, R.string.lenta_domen),
    RIA(R.drawable.ria, R.string.ria, R.string.ria_domen),
    MEDUSA(R.drawable.meduza, R.string.meduza, R.string.meduza_domen);

    private final int idIc;
    private final int idUnderText;
    private final int idDomen;

    Sources(int idIc, int idUnderText, int idDomen) {
        this.idIc = idIc;
        this.idUnderText = idUnderText;
        this.idDomen = idDomen;
    }

    public int getIdDomen() {
        return idDomen;
    }

    public int getIdIc() {
        return idIc;
    }

    public int getIdUnderText() {
        return idUnderText;
    }
}
