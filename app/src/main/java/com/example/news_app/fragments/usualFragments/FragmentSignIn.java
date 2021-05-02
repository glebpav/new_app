package com.example.news_app.fragments.usualFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news_app.ActivityNews;
import com.example.news_app.adapters.AdapterSettingTiles;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.databinding.FragmentSignInBinding;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;


public class FragmentSignIn extends Fragment {

    private MakeRequests requests;
    private FragmentSignIn thisFragment;
    private String login, password;
    private FragmentSignInBinding binding;
    private DialogFragmentProgressBar progressBar;

    final String url = "https://analisinf.pythonanywhere.com/";

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        binding.btnSignIn.setOnClickListener(btnSignInClicked);
        binding.btnSignUp.setOnClickListener(btnSignUpClicked);

        requests = new MakeRequests(url);
        thisFragment = this;

        checkUser();

        return binding.getRoot();
    }

    void checkUser() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        login = pref.getString("login", "");
        password = pref.getString("password", "");
        String status = "bad request";

        if (login.length() == 0 || password.length() == 0) return;

        progressBar = new DialogFragmentProgressBar();
        progressBar.show(getFragmentManager(), "FragmentSingIn");
        requests.new SignInRequest(login, password, singInListener).execute();

    }

    View.OnClickListener btnSignUpClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().add(R.id.MA, new FragmentSingUp()).commit();
        }
    };

    View.OnClickListener btnSignInClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            login = binding.etLogin.getText().toString();
            password = binding.etPassword.getText().toString();

            if (login.length() == 0) {
                Toast.makeText(getActivity(), "введите логин", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() == 0) {
                Toast.makeText(getActivity(), "введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar = new DialogFragmentProgressBar();
            assert getFragmentManager() != null;
            progressBar.show(getFragmentManager(), "FragmentSignIn");
            requests.new SignInRequest(login, password, singInListener).execute();
        }
    };

    public void gotoNextActivity(User user) {
        Intent intent = new Intent(getActivity(), ActivityNews.class);
        intent.putExtra("id", user.getId());
        intent.putExtra("name", user.getName());
        intent.putExtra("login", user.getLogin());
        intent.putExtra("password", user.getLogin());
        intent.putExtra("history", user.getHistory());
        intent.putExtra("themes", user.getThemes());
        intent.putExtra("password", user.getPassword());
        startActivity(intent);
    }

    MakeRequests.OnSingInListener singInListener = new MakeRequests.OnSingInListener() {
        @Override
        public void onSingIn(User user) {

            progressBar.dismiss();

            if (user == null){
                Toast.makeText(getContext(), getResources().getString(R.string.touble_with_autarisation), Toast.LENGTH_LONG).show();
                return;
            }

            if (binding.cbRememberMe.isChecked()) {
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("login", login);
                edt.putString("password", password);
                edt.apply();
            }
            gotoNextActivity(user);
        }
    };

}
