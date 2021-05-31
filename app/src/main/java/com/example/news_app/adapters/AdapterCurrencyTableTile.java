package com.example.news_app.adapters;

import android.annotation.SuppressLint;
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

    private ArrayList <CentBankCurrency> listCurrency;

    public void setListCurrency(ArrayList<CentBankCurrency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    public AdapterCurrencyTableTile(ArrayList<CentBankCurrency> listCurrency) {
        this.listCurrency = listCurrency;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CentBankCurrency currency = listCurrency.get(position);
        holder.binding.tvCurrencyName.setText(currency.getName());
        holder.binding.tvCurrencyCharCode.setText(currency.getCharCode());
        holder.binding.tvValue.setText(String.format("%.2f",currency.getValue()));
        if (!currency.isHidden()){
            holder.binding.btnAdd.setShapeType(ShapeType.PRESSED);
            holder.binding.btnAdd.setImageResource(R.drawable.ic_baseline_close_24);
        }

    }

    @Override
    public int getItemCount() {
        return listCurrency.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ItemCurrencyTableBinding binding;

        public ViewHolder(ItemCurrencyTableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
