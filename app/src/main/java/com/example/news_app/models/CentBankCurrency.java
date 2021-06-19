package com.example.news_app.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "CentBankCurrency")
public class CentBankCurrency {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "isHidden")
    private boolean isHidden;
    @ColumnInfo(name = "value")
    private double value;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "charCode")
    private String charCode;
    @ColumnInfo(name = "nominal")
    private Integer nominal;
    @ColumnInfo(name = "numCode")
    private Integer numCode;

    public CentBankCurrency(int id, boolean isHidden, double value, String name, String charCode, Integer nominal, Integer numCode) {
        this.id = id;
        this.isHidden = isHidden;
        this.value = value;
        this.name = name;
        this.charCode = charCode;
        this.nominal = nominal;
        this.numCode = numCode;
    }

    @Ignore
    public CentBankCurrency() {
        isHidden = true;
    }

    @Ignore
    public CentBankCurrency(double value, String name, String charCode, Integer nominal, Integer numCode) {
        this.value = value;
        this.name = name;
        this.charCode = charCode;
        this.nominal = nominal;
        this.numCode = numCode;
        isHidden = true;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public Integer getNominal() {
        return nominal;
    }

    public Integer getNumCode() {
        return numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public void setNominal(Integer nominal) {
        this.nominal = nominal;
    }

    public void setNumCode(Integer numCode) {
        this.numCode = numCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj.hashCode() == this.hashCode()) return true;
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        String str = "";
        str += name + " " + value;
        return str;
    }
}
