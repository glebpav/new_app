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
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.models.SavedData;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.databinding.FragmentSignInBinding;
import com.example.news_app.models.User;
import com.example.news_app.utils.SharedPreferencesHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static com.example.news_app.R.string.no_internet;


public class FragmentSignIn extends Fragment {

    private static final String TAG = "FRAGMENT_SING_IN_SPACE";

    private User mUser;
    private MakeRequests requests;
    private String login, password;
    private FragmentSignInBinding binding;
    private DialogFragmentProgressBar progressBar;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSignInBinding.inflate(inflater, container, false);

        binding.btnSignIn.setOnClickListener(btnSignInClicked);
        binding.btnSignUp.setOnClickListener(btnSignUpClicked);

        mUser = new User();
        requests = new MakeRequests();

        checkUser();

        return binding.getRoot();
    }

    private final MakeRequests.OnSingInListener singInListener = user -> {
        Log.d(TAG, "onSingIn: " + (user == null ? "null" : "not null"));
        if (progressBar.isAdded()) progressBar.dismiss();

        if (user == null) {
            loadDataFromPref();
            //savedData = jsonManager.readUserFromJson();

            /*
            Log.d(TAG, "login : " + mUser.getLogin());
            Log.d(TAG, "password : " + mUser.getPassword());
             */

            if (mUser.getLogin() == null || mUser.getPassword() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.trouble_with_authorization),
                        Toast.LENGTH_LONG).show();
                return;
            }

            /*
            if (binding.cbRememberMe.isChecked()) {
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("login", savedData.getLogin());
                edt.putString("password", savedData.getPassword());
                edt.apply();
            }
            Log.d("TAG", "onSingIn: " + savedData.getId());*/
            gotoNextActivity(mUser);
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
    };

    private final View.OnClickListener btnSignUpClicked = v -> {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().add(R.id.MA, new FragmentSingUp()).commit();
    };

    private final View.OnClickListener btnSignInClicked = v -> {
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

        if (!requests.isInternetAvailable(getContext())) {
            Toast.makeText(getContext(), no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar = new DialogFragmentProgressBar();
        assert getFragmentManager() != null;
        progressBar.show(getFragmentManager(), "FragmentSignIn");
        requests.new SignInRequest(login, password, singInListener).execute();
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

    void loadDataFromPref() {
        mUser.setName(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.name_key),
                getContext()));
        mUser.setLogin(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.login_key),
                getContext()));
        mUser.setPassword(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.password_key),
                getContext()));
    }

    private void checkUser() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        login = pref.getString("login", "");
        password = pref.getString("password", "");

        if (login.length() == 0 || password.length() == 0) return;

        progressBar = new DialogFragmentProgressBar();
        progressBar.show(getFragmentManager(), "FragmentSingIn");
        requests.new SignInRequest(login, password, singInListener).execute();

    }
}
