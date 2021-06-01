package com.example.news_app.fragments.dialogFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.news_app.adapters.AdapterCurrencyTableTile;
import com.example.news_app.comparators.currecy.ComparatorCurrencyAlphabet;
import com.example.news_app.comparators.currecy.ComparatorCurrencyChoice;
import com.example.news_app.databinding.DialogFragmentChangeCurrencyBinding;
import com.example.news_app.models.CentBankCurrency;

import java.util.ArrayList;
import java.util.Objects;

public class DialogFragmentSelectCurrency extends DialogFragment {

    private static final String TAG = "DIALOG_FRAGMENT_CURRENCY_SPACE";
    private final OnCurrencySelectedListener mCurrencySelectedListener;
    private ArrayList <CentBankCurrency> listCurrency;
    private DialogFragmentChangeCurrencyBinding binding;
    private AdapterCurrencyTableTile adapterCurrencyTableTile;
    private ComparatorCurrencyAlphabet comparatorAlphabet;
    private ComparatorCurrencyChoice comparatorChoice;

    public void setListCurrency(ArrayList<CentBankCurrency> listCurrency) {
        this.listCurrency = listCurrency;
        adapterCurrencyTableTile.setListCurrency(listCurrency);
        adapterCurrencyTableTile.notifyDataSetChanged();
    }

    @SuppressLint("LongLogTag")
    public DialogFragmentSelectCurrency(ArrayList<CentBankCurrency> listCurrency, OnCurrencySelectedListener mCurrencySelectedListener) {
        this.listCurrency = listCurrency;
        this.mCurrencySelectedListener = mCurrencySelectedListener;
        for (CentBankCurrency currency:listCurrency)
            Log.d(TAG, "DialogFragmentSelectCurrency: " + currency.isHidden());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentChangeCurrencyBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        adapterCurrencyTableTile = new AdapterCurrencyTableTile(listCurrency, currencySelectedListener);
        binding.recyclerView.setAdapter(adapterCurrencyTableTile);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comparatorAlphabet = new ComparatorCurrencyAlphabet();
        comparatorChoice  = new ComparatorCurrencyChoice();

        binding.tvSortAlphabet.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                listCurrency.sort(comparatorAlphabet);
                adapterCurrencyTableTile.setListCurrency(listCurrency);
                adapterCurrencyTableTile.notifyDataSetChanged();
                comparatorAlphabet.nextStage();
            }
        });

        binding.tvSortChoice.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                listCurrency.sort(comparatorChoice);
                adapterCurrencyTableTile.setListCurrency(listCurrency);
                adapterCurrencyTableTile.notifyDataSetChanged();
                comparatorChoice.nextStage();
            }
        });

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        return binding.getRoot();
    }

    AdapterCurrencyTableTile.OnCurrencySelectedListener currencySelectedListener = new AdapterCurrencyTableTile.OnCurrencySelectedListener() {
        @Override
        public void onSelected(CentBankCurrency currency) {
            mCurrencySelectedListener.onSelected(currency);

        }
    };

    public interface OnCurrencySelectedListener{
        void onSelected(CentBankCurrency currency);
    }

}
