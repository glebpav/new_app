package com.example.news_app.fragments;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.adapters.AdapterTrackingThemes;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.databinding.FragmentTrackingBinding;
import com.example.news_app.models.User;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FragmentTrackingTheme extends Fragment {

    public User user;
    public View view;
    public MakeRequests requests;
    public FragmentTrackingTheme fragment;
    public AlertDialog alertDialog;
    public AdapterTrackingThemes adapter;

    public ViewPager2 pager;
    public MeowBottomNavigation meow;
    public FragmentTrackingBinding binding;

    public String addingTheme;

    public FragmentTrackingTheme(ViewPager2 pager, User user, MeowBottomNavigation meow) {
        this.user = user;
        this.pager = pager;
        this.meow = meow;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTrackingBinding.inflate(inflater, container, false);

        binding.btnAddTrackingTheme.setOnClickListener(btn_add_theme_clicked);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(llm);

        clearThemes();
        adapter = new AdapterTrackingThemes( Arrays.asList(user.getThemes().split(";")),
                this, pager, meow);
        binding.recyclerView.setAdapter(adapter);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        fragment = this;

        return binding.getRoot();
    }

    View.OnClickListener btn_add_theme_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
            View view_dialog = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_theme,
                    (RelativeLayout) view.findViewById(R.id.add_theme_layout)
            );
            builder.setView(view_dialog);

            final TextInputEditText textInputEditText = view_dialog.findViewById(R.id.et_add_theme);
            Button btn_dismiss = view_dialog.findViewById(R.id.btn_dismiss);
            Button btn_apply = view_dialog.findViewById(R.id.btn_apply);

            alertDialog = builder.create();

            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            btn_dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            btn_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String theme = String.valueOf(textInputEditText.getText());
                    if (theme.length() == 0) {
                        Toast.makeText(getContext(), "Введите тему", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addingTheme = theme;
                    MakeRequests.AddTrackingTheme add_tracking_theme = requests.new AddTrackingTheme(fragment);
                    add_tracking_theme.execute();
                }
            });
            alertDialog.show();
        }
    };

    public void clearThemes() {
        user.setThemes(user.getThemes().replace(";;", ";"));
        if (user.getThemes().charAt(0) == ';') {
            user.setThemes(user.getThemes().substring(1));
        }
    }
}