package com.example.news_app.fragments.dialogFragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.fragment.app.FragmentManager;

import com.example.news_app.R;
import com.example.news_app.databinding.DialogFragmentHistoryBinding;
import com.example.news_app.databinding.DialogFragmentProgressBarBinding;

public class DialogFragmentProgressBar extends DialogFragment {

    DialogFragmentProgressBarBinding binding;

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        assert tag != null;
        Log.d("inFragmentDialog", tag);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.dialog_fragment_progress_bar, container, false);
        binding = DialogFragmentProgressBarBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        Log.d("asd" , "I was taken");
        return binding.getRoot();
    }
}
