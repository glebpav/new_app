package com.example.news_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.textfield.TextInputEditText;

public class Settings extends Fragment {

    User user;
    View view;
    MakeRequests requests;
    ArrayAdapter<String> adapter_history;

    TextView tv_name, tv_count_themes, tv_count_tracking;
    Button btn_sign_out, btn_change_name;
    ProgressBar progressBar;
    RelativeLayout layout_error, layout_success;
    ListView listView;
    ViewPager2 pager;
    MeowBottomNavigation meow;

    public Settings(User user, ViewPager2 pager, MeowBottomNavigation meow) {
        this.user = user;
        this.meow = meow;
        this.pager = pager;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        tv_count_themes = view.findViewById(R.id.tv_count_themes);
        tv_count_tracking = view.findViewById(R.id.tv_count_tracking);
        tv_name = view.findViewById(R.id.tv_name_user);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        btn_change_name = view.findViewById(R.id.btn_change_name);
        layout_error = view.findViewById(R.id.layout_error);
        layout_success = view.findViewById(R.id.layout_success);
        progressBar = view.findViewById(R.id.progress_circular);
        listView = view.findViewById(R.id.lv_history);

        btn_sign_out.setOnClickListener(btn_sign_out_clicked);
        btn_change_name.setOnClickListener(btn_change_name_clicked);

        tv_name.setText(user.name);

        tv_count_tracking.setText(String.valueOf(user.themes.split(";").length));
        tv_count_themes.setText(String.valueOf(user.history.split(";").length));

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");

        return view;
    }

    View.OnClickListener btn_sign_out_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

            Log.d("asd",pref.getString("login", "z") + "aa");
            Log.d("asd",pref.getString("password", "z") + "aa");

            SharedPreferences.Editor edt = pref.edit();
            edt.putString("login", "");
            edt.putString("password", "");
            edt.commit();

            Log.d("asd",pref.getString("login", ""));
            Log.d("asd",pref.getString("password", ""));


            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener btn_change_name_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
            View view_dialog = LayoutInflater.from(getActivity()).inflate(
                    R.layout.change_name,
                    (RelativeLayout) view.findViewById(R.id.layout_dialog_container)
            );
            builder.setView(view_dialog);

            final TextInputEditText textInputEditText = view_dialog.findViewById(R.id.et_change_name);
            Button btn_dismiss = view_dialog.findViewById(R.id.btn_dismiss);
            Button btn_apply = view_dialog.findViewById(R.id.btn_apply);

            textInputEditText.setText(user.name);

            final AlertDialog alertDialog = builder.create();

            if (alertDialog.getWindow() != null) alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            btn_dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            btn_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = String.valueOf(textInputEditText.getText());
                    user.name = name;
                    tv_name.setText(user.name);
                    requests.change_user(user);
                    alertDialog.dismiss();
                    Toast.makeText(getActivity(), "Имя пользователя изменено успешно", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
        }
    };

    void change_fields(final User user){
        tv_count_tracking.setText(String.valueOf(user.themes.split(";").length));
        tv_count_themes.setText(String.valueOf(user.history.split(";").length));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, user.history.split(";"));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("SearchingTheme", user.history.split(";")[position]);
                edt.commit();

                pager.setCurrentItem(0);
                meow.show(0, true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MakeRequests.Load_user load_user = requests.new Load_user(this);
        load_user.execute();
    }
}