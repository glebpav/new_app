package com.example.news_app.fragments.usualFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news_app.databinding.FragmentSingUpBinding;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;


public class FragmentSingUp extends Fragment {

    private MakeRequests requests;
    private DialogFragmentProgressBar progressBar;

    private String login, password, name;
    private FragmentSingUpBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingUpBinding.inflate(inflater, container, false);

        binding.btnSignUp.setOnClickListener(btnSingUpClicked);
        binding.btnBack.setOnClickListener(btnBackClicked);

        requests = new MakeRequests();

        return binding.getRoot();
    }


    View.OnClickListener btnSingUpClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            login = binding.etLogin.getText().toString();
            password = binding.etPassword.getText().toString();
            name = binding.etName.getText().toString();

            if (!validateLogin(login) || !validatePassword(password))return;
            if (name.length() == 0) {
                Toast.makeText(getContext(), "введите имя", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar = new DialogFragmentProgressBar();
            progressBar.show(getFragmentManager(), "FragmentSingUp");
            binding.btnBack.setClickable(false);
            binding.btnSignUp.setClickable(false);
            MakeRequests.SignUpRequest sign_upRequest = requests.new SignUpRequest(signUpListener,
                    binding.etLogin.getText().toString(),
                    binding.etName.getText().toString(),
                    binding.etPassword.getText().toString());
            sign_upRequest.execute();
        }
    };

    View.OnClickListener btnBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction().add(R.id.MA, new FragmentSignIn()).commit();
        }
    };

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
            Toast.makeText(getContext(), getResources().getString(responseId), Toast.LENGTH_SHORT).show();
        }
    };

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