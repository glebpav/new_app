package com.example.news_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Sing_up extends Fragment {

    ImageButton btn_sing_up;
    Button btn_back;
    EditText et_login, et_password, et_name;
    ProgressBar progress_bar;

    MakeRequests requests;
    Sing_up sing_up_fragment;

    String login, password, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sing_up, container, false);

        btn_back = view.findViewById(R.id.btn_back);
        btn_sing_up = view.findViewById(R.id.btn_sign_up);
        et_login = view.findViewById(R.id.et_login);
        et_name = view.findViewById(R.id.et_name);
        et_password = view.findViewById(R.id.et_password);
        progress_bar = view.findViewById(R.id.progress_circular);

        btn_sing_up.setOnClickListener(btn_sing_up_clicked);
        btn_back.setOnClickListener(btn_back_clicked);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");

        sing_up_fragment = this;
        return view;
    }


    View.OnClickListener btn_sing_up_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("asd", String.valueOf(et_password.getText()) + " dfvdf");
            if (et_password.getText().length() == 0) {
                Toast.makeText(getContext(), "введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_login.getText().length() == 0) {
                Toast.makeText(getContext(), "введите логин", Toast.LENGTH_SHORT).show();
                return;
            }
            if (et_name.getText().length() == 0) {
                Toast.makeText(getContext(), "введите имя", Toast.LENGTH_SHORT).show();
                return;
            }

            login = et_login.getText().toString();
            password = et_password.getText().toString();
            name = et_name.getText().toString();

            MakeRequests.Sign_up_request sign_up_request = requests.new Sign_up_request(sing_up_fragment);
            sign_up_request.execute();
        }
    };

    View.OnClickListener btn_back_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction().add(R.id.MA, new Sign_in()).commit();
        }
    };
}