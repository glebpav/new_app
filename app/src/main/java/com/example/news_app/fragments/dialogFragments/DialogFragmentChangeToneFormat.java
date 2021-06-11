package com.example.news_app.fragments.dialogFragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.news_app.R;
import com.example.news_app.databinding.DialogFragmentChangeFormatBinding;

import static android.content.Context.MODE_PRIVATE;

public class DialogFragmentChangeToneFormat extends DialogFragment {

    private static final String TAG = "DIALOG_FRAGMENT_CHANGE_TONE_FORMAT_SPACE";
    DialogFragmentChangeFormatBinding binding;

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentChangeFormatBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding.btnApply.setOnClickListener(v -> getDialog().dismiss());
        binding.chipRegarding.setOnClickListener(chipRegardingClicked);
        binding.chipNumber.setOnClickListener(chipNumberingClicked);

        String toneFormat = getToneFormatFromPref();
        Log.d(TAG, "onCreateView: " + toneFormat);
        if (toneFormat == null || toneFormat.equals("REG")){
            binding.chipRegarding.setChecked(true);
            binding.chipNumber.setChecked(false);
            binding.tvToneFormatDesc.setText(getResources().getString(R.string.regarding_format_desc));
        }else{
            binding.chipRegarding.setChecked(false);
            binding.chipNumber.setChecked(true);
            binding.tvToneFormatDesc.setText(getResources().getString(R.string.number_format_desc));
        }

        return binding.getRoot();
    }

    private final View.OnClickListener chipRegardingClicked = v -> {
        binding.tvToneFormatDesc.setText(getResources().getString(R.string.regarding_format_desc));
        putToneFormatToSharePref("REG");
    };

    private final View.OnClickListener chipNumberingClicked = v -> {
        binding.tvToneFormatDesc.setText(getResources().getString(R.string.number_format_desc));
        putToneFormatToSharePref("NUM");
    };

    private void putToneFormatToSharePref(String outputStr){
        SharedPreferences toneFormatPreferences = getContext().getSharedPreferences(getResources().
                getString(R.string.tone_format), MODE_PRIVATE);
        toneFormatPreferences.edit().putString(getResources().
                getString(R.string.tone_format_key), outputStr).apply();
    }

    private String getToneFormatFromPref(){
        SharedPreferences toneTypePreferences = getContext().getSharedPreferences(getResources().
                getString(R.string.tone_format), MODE_PRIVATE);
        return toneTypePreferences.getString(getResources().
                getString(R.string.tone_format_key), null);
    }
}
