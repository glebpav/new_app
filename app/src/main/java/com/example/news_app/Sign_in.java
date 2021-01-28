package com.example.news_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Sign_in extends Fragment {

    ImageButton btn_sign_in;
    Button btn_sign_up;
    EditText et_login, et_password;
    CheckBox cb_remember_me;
    ProgressBar progress_bar;

    MakeRequests requests;
    String login, password;

    final String url = "https://analisinf.pythonanywhere.com/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        btn_sign_in = view.findViewById(R.id.btn_sign_in);
        btn_sign_up = view.findViewById(R.id.btn_sign_up);
        et_login = view.findViewById(R.id.et_login);
        et_password = view.findViewById(R.id.et_password);
        cb_remember_me = view.findViewById(R.id.cb_remember_me);
        progress_bar = view.findViewById(R.id.progress_circular);

        btn_sign_in.setOnClickListener(btn_sign_in_clicked);
        btn_sign_up.setOnClickListener(btn_sign_up_clicked);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");

        check_user();

        return view;
    }

    void check_user() {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        login = pref.getString("login", "");
        password = pref.getString("password", "");
        String status = "bad request";

        //String server_request = requests.sign_in(login, password);
        Log.d("asd", login + " - " + login.length());
        Log.d("asd", password + " - " + password.length());

        if (login.length() == 0 || password.length() == 0) return;

        Log.d("asd", "here");

        MakeRequests.Sign_in_request sign_in_request = requests.new Sign_in_request(this);
        sign_in_request.execute();

    }

    View.OnClickListener btn_sign_up_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction().add(R.id.MA, new Sing_up()).commit();
        }
    };

    View.OnClickListener btn_sign_in_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String status = "bad request", trouble = "something went wrong";

            if (et_login.getText().length() == 0) {
                Toast.makeText(getActivity(), "введите логин", Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_password.getText().length() == 0) {
                Toast.makeText(getActivity(), "введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            String server_request = requests.sign_in(et_login.getText().toString(), et_password.getText().toString());
            Log.d("response", server_request);

            String jsonString = server_request;
            JSONObject obj = null;
            try {
                obj = new JSONObject(jsonString);
                status = obj.getString("status");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equals("ok")) {
                if (cb_remember_me.isChecked()) {
                    SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putString("login", et_login.getText().toString());
                    edt.putString("password", et_password.getText().toString());
                    edt.commit();
                }
                try {
                    User user = serialize_User(obj.getString("user"));
                    goto_next_activity(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                try {
                    trouble = obj.getString("trouble");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), trouble, Toast.LENGTH_SHORT).show();
            }

        }
    };

    void goto_next_activity(User user) {
        Intent intent = new Intent(getActivity(), News_activity.class);
        intent.putExtra("id", user.id);
        intent.putExtra("name", user.name);
        intent.putExtra("login", user.login);
        intent.putExtra("password", user.password);
        intent.putExtra("history", user.history);
        intent.putExtra("themes", user.themes);
        intent.putExtra("password", user.password);
        startActivity(intent);
    }

    User serialize_User(String jsonString) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
            return new User(Integer.valueOf(obj.getString("id")), obj.getString("name"), obj.getString("login"), obj.getString("password"), obj.getString("history"), obj.getString("themes"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
