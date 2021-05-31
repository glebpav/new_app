package com.example.news_app.comparators.currecy;

import com.example.news_app.models.CentBankCurrency;

import java.util.Comparator;

public class ComparatorCurrencyAlphabet implements Comparator<CentBankCurrency> {

    private boolean stage;

    public ComparatorCurrencyAlphabet() {
        stage = true;
    }

    public ComparatorCurrencyAlphabet(boolean stage) {
        this.stage = stage;
    }

    @Override
    public int compare(CentBankCurrency o1, CentBankCurrency o2) {
        if (stage)
            return o1.getName().compareTo(o2.getName());
        return -1 * o1.getName().compareTo(o2.getName());
    }

    public void nextStage() {
        stage = !stage;
    }
}
