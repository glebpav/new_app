package com.example.news_app.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.ActivityMain;
import com.example.news_app.databinding.FragmentSettingsBinding;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.models.User;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


 // Todo :: load information from site


public class FragmentSettings extends Fragment {

    User user;
    View view;
    MakeRequests requests;
    ArrayAdapter<String> adapter_history;
    public FragmentSettingsBinding binding;

    TextView tv_name, tv_count_themes, tv_count_tracking;
    Button btn_sign_out, btn_change_name;
    ProgressBar progressBar;
    RelativeLayout layout_error, layout_success;
    ListView listHistory;
    ViewPager2 pager;
    MeowBottomNavigation meow;

    public FragmentSettings(User user, ViewPager2 pager, MeowBottomNavigation meow) {
        this.user = user;
        this.meow = meow;
        this.pager = pager;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        String name = "hello";

        /*Toolbar collapsingToolbarLayout = view.findViewById(R.id.toolbar);
        collapsingToolbarLayout.setTitle(name);*/

        /*tv_count_themes = view.findViewById(R.id.tv_count_themes);
        tv_count_tracking = view.findViewById(R.id.tv_count_tracking);
        tv_name = view.findViewById(R.id.tv_name_user);
        btn_sign_out = view.findViewById(R.id.btn_sign_out);
        btn_change_name = view.findViewById(R.id.btn_change_name);
        layout_error = view.findViewById(R.id.layout_error);
        layout_success = view.findViewById(R.id.layout_success);
        progressBar = view.findViewById(R.id.progress_circular);
        listHistory = view.findViewById(R.id.lv_history);

        btn_sign_out.setOnClickListener(btn_sign_out_clicked);
        btn_change_name.setOnClickListener(btn_change_name_clicked);

        tv_name.setText(user.name);

        tv_count_tracking.setText(String.valueOf(user.themes.split(";").length));
        tv_count_themes.setText(String.valueOf(user.history.split(";").length));

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
*/
        return binding.getRoot();
    }

    View.OnClickListener btn_sign_out_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("login", "");
            edt.putString("password", "");
            edt.apply();
            Log.d("asd",pref.getString("login", ""));
            Log.d("asd",pref.getString("password", ""));
            Intent intent = new Intent(getActivity(), ActivityMain.class);
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

            textInputEditText.setText(user.getName());

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
                    user.setName(name);
                    tv_name.setText(user.getName());
                    requests.changeUser(user);
                    alertDialog.dismiss();
                    Toast.makeText(getActivity(), "Имя пользователя изменено успешно", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.show();
        }
    };

    void change_fields(final User user){
        tv_count_tracking.setText(String.valueOf(user.getThemes().split(";").length));
        tv_count_themes.setText(String.valueOf(user.getHistory().split(";").length));
        //if (user.history.equals(";"))user.history = null;
        List <String> list = Arrays.asList(user.getHistory().split(";"));
        System.out.println(list);
        Collections.reverse(list);
        System.out.println(list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, list.toArray(new String[0]));
        listHistory.setAdapter(adapter);

        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("SearchingTheme", user.getHistory().split(";")
                        [(user.getHistory().split(";")).length - position - 1]);
                edt.commit();

                pager.setCurrentItem(0);
                meow.show(0, true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /*MakeRequests.Load_user load_user = requests.new Load_user(this);
        load_user.execute();*/
    }
}