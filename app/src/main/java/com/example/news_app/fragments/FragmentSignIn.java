package com.example.news_app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news_app.ActivityNews;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.databinding.FragmentSignInBinding;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;


public class FragmentSignIn extends Fragment {

   /* public ImageButton btn_sign_in;
    public Button btn_sign_up;
    public EditText et_login, et_password;
    public CheckBox cb_remember_me;
    public ProgressBar progress_bar;*/

    public MakeRequests requests;
    public FragmentSignIn thisFragment;
    public String login, password;
    public FragmentSignInBinding binding;

    final String url = "https://analisinf.pythonanywhere.com/";

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        binding.btnSignIn.setOnClickListener(btnSignInClicked);
        binding.btnSignUp.setOnClickListener(btnSignUpClicked);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        thisFragment = this;

        check_user();

        return binding.getRoot();
    }

    void check_user() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        login = pref.getString("login", "");
        password = pref.getString("password", "");
        String status = "bad request";

        Log.d("asd", login + " - " + login.length());
        Log.d("asd", password + " - " + password.length());

        if (login.length() == 0 || password.length() == 0) return;

        Log.d("asd", "here");

        MakeRequests.SignInRequest sign_inRequest = requests.new SignInRequest(this);
        sign_inRequest.execute();

    }

    View.OnClickListener btnSignUpClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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

            MakeRequests.SignInRequest sign_inRequest = requests.new SignInRequest(thisFragment, true);
            sign_inRequest.execute();
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

}
