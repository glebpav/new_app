package com.example.news_app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.ActivityMain;
import com.example.news_app.adapters.AdapterSettingTiles;
import com.example.news_app.databinding.FragmentSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.models.User;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class FragmentSettings extends Fragment {

    User user;
    MakeRequests requests;
    ArrayAdapter<String> adapter_history;
    AdapterSettingTiles adapterSettingTiles;
    FragmentSettingsBinding binding;

    ListView listHistory;
    ViewPager2 pager;
    MeowBottomNavigation meow;
    ProgressDialog progressDialog;

    public FragmentSettings(User user, ViewPager2 pager, MeowBottomNavigation meow) {
        this.user = user;
        this.meow = meow;
        this.pager = pager;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");

        adapterSettingTiles = new AdapterSettingTiles(getContext(), onClickedSettingsItemListener);
        binding.recyclerViewSettings.setAdapter(adapterSettingTiles);
        binding.recyclerViewSettings.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return binding.getRoot();
    }

    private void initFragment() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        binding.appbar.setVisibility(View.INVISIBLE);
        binding.nestedScrollView.setVisibility(View.INVISIBLE);
    }

    AdapterSettingTiles.OnClickedSettingsItemListener onClickedSettingsItemListener = new
            AdapterSettingTiles.OnClickedSettingsItemListener() {
                @Override
                public void onClicked(SettingsPoints point) {
                    switch (point) {
                        case LOG_OUT:
                            logOut();
                            break;
                        case CHANGE_NAME:
                            changeName();
                            break;
                        case SHOW_HISTORY:
                            showHistory();
                            break;
                    }
                }
            };

    void logOut() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("login", "");
        edt.putString("password", "");
        edt.apply();
        Log.d("asd", pref.getString("login", ""));
        Log.d("asd", pref.getString("password", ""));
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        startActivity(intent);
    }

    void changeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        View view_dialog = LayoutInflater.from(getActivity()).inflate(
                R.layout.change_name,
                (RelativeLayout) binding.getRoot().findViewById(R.id.layout_dialog_container)
        );
        builder.setView(view_dialog);

        final TextInputEditText textInputEditText = view_dialog.findViewById(R.id.et_change_name);
        Button btn_dismiss = view_dialog.findViewById(R.id.btn_dismiss);
        Button btn_apply = view_dialog.findViewById(R.id.btn_apply);

        textInputEditText.setText(user.getName());

        final AlertDialog alertDialog = builder.create();

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
                String name = String.valueOf(textInputEditText.getText());
                user.setName(name);
                binding.toolbar.setTitle(user.getName());
                requests.changeUser(user);
                alertDialog.dismiss();
                Toast.makeText(getActivity(), "Имя пользователя изменено успешно", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    void showHistory() {
        DialogFragmentHistory fragmentHistory = new DialogFragmentHistory();
        fragmentHistory.show(getFragmentManager(), "FragmentDialog");
    }

    void change_fields(final User user) {
        binding.tvCountTracking.setText(String.valueOf(user.getThemes().split(";").length));
        binding.tvCountThemes.setText(String.valueOf(user.getHistory().split(";").length));
        //if (user.history.equals(";"))user.history = null;
        List<String> list = Arrays.asList(user.getHistory().split(";"));
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

        initFragment();

        MakeRequests.OnLoadUserListener loadUserListener = new MakeRequests.OnLoadUserListener() {
            @Override
            public void onResults(User user) {
                binding.collapsingToolbar.setTitle(user.getName());
                binding.tvCountThemes.setText((!user.getHistory().isEmpty()) ?
                        String.valueOf(user.getHistory().split(";").length) : "0");
                binding.tvCountTracking.setText((!user.getThemes().isEmpty()) ?
                        String.valueOf(user.getThemes().split(";").length) : "0");

                progressDialog.dismiss();
                binding.nestedScrollView.setVisibility(View.VISIBLE);
                binding.appbar.setVisibility(View.VISIBLE);
            }
        };


        requests.new LoadUser(user.getLogin(), user.getPassword(), loadUserListener).execute();
    }


}