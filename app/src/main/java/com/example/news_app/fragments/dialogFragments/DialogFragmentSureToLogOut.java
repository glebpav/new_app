package com.example.news_app.fragments.dialogFragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.example.news_app.R;
import com.example.news_app.databinding.DialogFragmentSureToLogOutBinding;

public class DialogFragmentSureToLogOut extends DialogFragment {

    private DialogFragmentSureToLogOutBinding binding;
    private OnLogOutBtnClickedListener logOutBtnClickedListener;

    public void setLogOutBtnClickedListener(OnLogOutBtnClickedListener logOutBtnClickedListener) {
        this.logOutBtnClickedListener = logOutBtnClickedListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DialogFragmentSureToLogOutBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding.btnApply.setOnClickListener(btnApplyClicked);
        binding.btnDismiss.setOnClickListener(btnDismissClicked);

        return binding.getRoot();
    }

    View.OnClickListener btnApplyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logOutBtnClickedListener.onClicked();
        }
    };

    View.OnClickListener btnDismissClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().dismiss();
        }
    };

    public interface OnLogOutBtnClickedListener{
        void onClicked();
    }
}
