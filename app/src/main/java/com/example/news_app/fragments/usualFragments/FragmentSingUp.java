package com.example.news_app.fragments.usualFragments;

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

import com.example.news_app.activities.ActivityNews;
import com.example.news_app.databinding.FragmentSingUpBinding;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.models.User;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.utils.SharedPreferencesHelper;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static com.example.news_app.R.string.no_internet;


public class FragmentSingUp extends Fragment {

    private static final String TAG = "FRAGMENT_SING_UP_SPACE";

    private MakeRequests requests;
    private DialogFragmentProgressBar progressBar;

    private String login;
    private String password;
    private String name;

    private FragmentSingUpBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingUpBinding.inflate(inflater, container, false);

        binding.btnSignUp.setOnClickListener(btnSingUpClicked);
        binding.btnBack.setOnClickListener(btnBackClicked);

        requests = new MakeRequests();

        return binding.getRoot();
    }

    MakeRequests.OnSignUpListener signUpListener = new MakeRequests.OnSignUpListener() {
        @Override
        public void onClick(String response) {
            binding.btnSignUp.setClickable(true);
            binding.btnBack.setClickable(true);
            progressBar.dismiss();
            Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(int responseId) {
            binding.btnSignUp.setClickable(true);
            binding.btnBack.setClickable(true);
            progressBar.dismiss();

            if (responseId == R.string.user_created_successful) {
                requests.new SignInRequest(login, password, singInListener).execute();
            }

            Toast.makeText(getContext(), getResources().getString(responseId), Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener btnSingUpClicked = v -> {
        login = binding.etLogin.getText().toString();
        password = binding.etPassword.getText().toString();
        name = binding.etName.getText().toString();

        if (!validateLogin(login) || !validatePassword(password)) return;
        if (name.length() == 0) {
            Toast.makeText(getContext(), "введите имя", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar = new DialogFragmentProgressBar();
        progressBar.show(getFragmentManager(), "FragmentSingUp");
        binding.btnBack.setClickable(false);
        binding.btnSignUp.setClickable(false);

        if (!requests.isInternetAvailable(getContext())) {
            Toast.makeText(getContext(), no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        MakeRequests.SignUpRequest signUpRequest = requests.new SignUpRequest(signUpListener,
                binding.etLogin.getText().toString(),
                binding.etName.getText().toString(),
                binding.etPassword.getText().toString());
        signUpRequest.execute();
    };

    View.OnClickListener btnBackClicked = v -> {
        getFragmentManager().beginTransaction().add(R.id.MA, new FragmentSignIn()).commit();
    };

    private final MakeRequests.OnSingInListener singInListener = user -> {
        Log.d(TAG, "onSingIn: " + (user == null ? "null" : "not null"));
        if (progressBar.isAdded()) progressBar.dismiss();

        if (user == null) {
            return;
        }

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("login", user.getLogin());
        edt.putString("password", user.getPassword());
        edt.apply();

        gotoNextActivity(user);
    };


    public void gotoNextActivity(User user) {
        Intent intent = new Intent(getActivity(), ActivityNews.class);
        intent.putExtra("id", user.getId());
        intent.putExtra("name", user.getName());
        intent.putExtra("login", user.getLogin());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("themes", user.getThemes());
        intent.putExtra("history", user.getHistory());
        intent.putExtra("sites", user.getSites());
        intent.putExtra("currency", user.getCurrency());

        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.name_key),
                user.getName(),
                getContext());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.login_key),
                user.getLogin(),
                getContext());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.password_key),
                user.getPassword(),
                getContext());

        startActivity(intent);
    }

    boolean validateLogin(String login) {
        if (login.isEmpty()) {
            Toast.makeText(getContext(), "введите логин", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    boolean validatePassword(String password) {
        final Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[0-9])" +
                        "(?=.*[a-zA-Z])" +
                        ".{6,}" +
                        "$");

        if (password.isEmpty()) {
            Toast.makeText(getContext(), R.string.enter_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(getContext(), R.string.password_error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}