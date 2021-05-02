package com.example.news_app.fragments.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.news_app.R;
import com.example.news_app.databinding.DialogFragmentAddThemeBinding;

public class DialogFragmentAddTheme extends DialogFragment {

    OnBtnApplyClickListener btnApplyListener;
    DialogFragmentAddThemeBinding binding;

    public DialogFragmentAddTheme(OnBtnApplyClickListener btnApplyListener) {
        this.btnApplyListener = btnApplyListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.dialog_fragment_add_theme, container, false);
        binding = DialogFragmentAddThemeBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding.btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theme = binding.etAddTheme.getText().toString();
                Log.d("asd", "onClick: "+ theme);
                if (theme.isEmpty()) {
                    Toast.makeText(getContext(), "Введите тему", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnApplyListener.onClick(theme);
            }
        });

        return binding.getRoot();
    }

    public interface OnBtnApplyClickListener{
        void onClick(String theme);
    }

}
