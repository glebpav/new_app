package com.example.news_app.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.databinding.ItemCurrencyBinding;
import com.example.news_app.models.CentBankCurrency;

import java.util.ArrayList;

public class AdapterCurrencyTile extends RecyclerView.Adapter<AdapterCurrencyTile.ViewHolder>{

    private ArrayList<CentBankCurrency> listCurrency;

    public void setListCurrency(ArrayList<CentBankCurrency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    public AdapterCurrencyTile(ArrayList<CentBankCurrency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCurrencyBinding binding = ItemCurrencyBinding.inflate
                (LayoutInflater.from(parent.getContext()),parent, false);

        return new ViewHolder(binding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CentBankCurrency currency = listCurrency.get(position);
        holder.binding.tvCurrencyName.setText(currency.getCharCode());
        holder.binding.tvCurrencyValue.setText(String.format("%.2f", currency.getValue()));
    }

    @Override
    public int getItemCount() {
        if (listCurrency == null) return 0;
        return listCurrency.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemCurrencyBinding binding;

        public ViewHolder(ItemCurrencyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
