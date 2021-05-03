package com.example.news_app.fragments.dialogFragments;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.news_app.R;
import com.example.news_app.adapters.AdapterSourcesTiles;
import com.example.news_app.databinding.DialogFragmentSourcesBinding;
import com.example.news_app.enums.Sources;
import com.example.news_app.models.User;

public class DialogFragmentSources extends DialogFragment {

    DialogFragmentSourcesBinding binding;
    AdapterSourcesTiles adapterSourcesTiles;

    User user;

    public void setUser(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.dialog_fragment_sources, container, false);
        binding = DialogFragmentSourcesBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        adapterSourcesTiles = new AdapterSourcesTiles(user, getDialog().getContext(), sourcesItemListener);
        binding.recyclerView.setAdapter(adapterSourcesTiles);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getDialog().getContext(), 2));

        binding.btnApply.setOnClickListener(btnApplyClicked);
        return binding.getRoot();
    }


    View.OnClickListener btnApplyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().dismiss();
        }
    };

    AdapterSourcesTiles.OnClickedSourcesItemListener sourcesItemListener = new AdapterSourcesTiles.OnClickedSourcesItemListener() {
        @Override
        public void onClicked(Sources point) {

        }
    };


}
