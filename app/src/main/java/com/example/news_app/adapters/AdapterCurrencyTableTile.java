package com.example.news_app.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.R;
import com.example.news_app.databinding.ItemCurrencyTableBinding;
import com.example.news_app.models.CentBankCurrency;

import java.util.ArrayList;

import soup.neumorphism.ShapeType;

public class AdapterCurrencyTableTile extends RecyclerView.Adapter<AdapterCurrencyTableTile.ViewHolder> {

    private static final String TAG = "ADAPTER_CURRENCY_SPACE";
    private final OnCurrencySelectedListener currencySelectedListener;
    private ArrayList<CentBankCurrency> listCurrency;

    public void setListCurrency(ArrayList<CentBankCurrency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    public AdapterCurrencyTableTile(ArrayList<CentBankCurrency> listCurrency, OnCurrencySelectedListener currencySelectedListener) {
        this.listCurrency = listCurrency;
        this.currencySelectedListener = currencySelectedListener;

        for (CentBankCurrency currency: listCurrency){
            Log.d(TAG, "AdapterCurrencyTableTile: " + currency.getCharCode() +  " - " + currency.isHidden());
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCurrencyTableBinding binding = ItemCurrencyTableBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CentBankCurrency currency = listCurrency.get(position);
        holder.binding.tvCurrencyName.setText(currency.getName());
        holder.binding.tvCurrencyCharCode.setText(currency.getCharCode());
        holder.binding.tvValue.setText(String.format("%.2f", currency.getValue()));

        if (!currency.isHidden()) {
            holder.binding.btnAdd.setShapeType(ShapeType.PRESSED);
            holder.binding.btnAdd.setImageResource(R.drawable.ic_baseline_close_24);
        }
        else {
            holder.binding.btnAdd.setShapeType(ShapeType.DEFAULT);
            holder.binding.btnAdd.setImageResource(R.drawable.ic_baseline_add_24);
        }

        holder.binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencySelectedListener.onSelected(listCurrency.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCurrency.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemCurrencyTableBinding binding;

        public ViewHolder(ItemCurrencyTableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnCurrencySelectedListener {
        void onSelected(CentBankCurrency currency);
    }

}
