package com.example.news_app.comparators.currecy;

import com.example.news_app.models.CentBankCurrency;

import java.util.Comparator;

public class ComparatorCurrencyChoice implements Comparator<CentBankCurrency> {

    private boolean stage;

    public ComparatorCurrencyChoice() {
        stage = true;
    }

    @Override
    public int compare(CentBankCurrency o1, CentBankCurrency o2) {
        if (o1.isHidden() && o2.isHidden()) return new ComparatorCurrencyAlphabet(stage).compare(o1, o2);
        if (!o1.isHidden() && !o2.isHidden()) return new ComparatorCurrencyAlphabet(stage).compare(o1, o2);
        if (o1.isHidden() && !o2.isHidden() && !stage) return -1;
        if (o1.isHidden() && !o2.isHidden() && stage) return 1;
        if (!o1.isHidden() && o2.isHidden() && !stage) return 1;
        if (!o1.isHidden() && o2.isHidden() && stage) return -1;

        return 0;
    }

    public void nextStage(){
        stage = !stage;
    }
}
