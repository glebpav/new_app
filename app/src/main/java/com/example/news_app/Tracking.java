package com.example.news_app;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Set;

import soup.neumorphism.NeumorphButton;

public class Tracking extends Fragment {

    User user;
    View view;
    MakeRequests requests;
    Tracking fragment;
    AlertDialog alertDialog;
    Tracking_themes_adapter adapter;

    RecyclerView recycler_view;
    NeumorphButton btn_add_theme;
    ProgressBar progress_bar_alert, progress_bar;
    ViewPager2 pager;
    MeowBottomNavigation meow;

    String adding_theme;

    Tracking(ViewPager2 pager, User user, MeowBottomNavigation meow) {
        this.user = user;
        this.pager = pager;
        this.meow = meow;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tracking, container, false);

        recycler_view = view.findViewById(R.id.recycle_view);
        btn_add_theme = view.findViewById(R.id.btn_add_tracking_theme);
        progress_bar = view.findViewById(R.id.progress_circular);

        btn_add_theme.setOnClickListener(btn_add_theme_clicked);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(llm);

        clearThemes();
        adapter  = new Tracking_themes_adapter(serialise_themes(user.themes.split(";")), this, pager, meow);
        recycler_view.setAdapter(adapter);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        fragment = this;

        return view;
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
            progress_bar_alert = view_dialog.findViewById(R.id.progress_circular_alert);

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
                        Toast.makeText(getContext(), "Введите тему", Toast.LENGTH_SHORT);
                        return;
                    }

                    adding_theme = theme;
                    MakeRequests.Add_tracking_theme add_tracking_theme = requests.new Add_tracking_theme(fragment);
                    add_tracking_theme.execute();
                }
            });

            alertDialog.show();
        }
    };

    ArrayList<String> serialise_themes(String[] str) {
        ArrayList<String> strings_list = new ArrayList<>();
        for (String str_one : str) {
            strings_list.add(str_one);
        }
        return strings_list;
    }

    void clearThemes(){
        try {
            user.themes = user.themes.replace(";;", ";");
            if (user.themes.charAt(0) == ';'){
                user.themes = user.themes.substring(1);
            }
        }catch (Exception e){}
    }
}