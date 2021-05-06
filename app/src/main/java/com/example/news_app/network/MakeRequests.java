package com.example.news_app.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.news_app.R;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeRequests {

    final static String MAIN_URL = "https://analisinf.pythonanywhere.com/";

    public class SignInRequest extends AsyncTask<Void, Void, String> {

        private String login, password;
        private OnSingInListener listener;

        public SignInRequest(String login, String password, OnSingInListener listener) {
            this.login = login;
            this.password = password;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            String json = "{\"user\":{ \"login\": \"" + login + "\",\"password\": \"" + password + "\"}}";
            Log.d("json", json);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((MAIN_URL + "api/user_check/")).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response_str;
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    User user = User.serializeUser(obj.getString("user"));
                    listener.onSingIn(user);
                    return;
/*
                    if (fragment.binding.cbRememberMe.isChecked()) {
                        SharedPreferences pref = fragment.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edt = pref.edit();
                        edt.putString("login", fragment.login);
                        edt.putString("password", fragment.password);
                        edt.apply();
                    }
                    User user = null;
                    try {
                        user = serializeUser(obj.getString("user"));
                        fragment.gotoNextActivity(user);
                        fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

 */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onSingIn(null);
            /*
            fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Возникла проблема с автоматической авторизацией", Toast.LENGTH_LONG).show();*/
        }
    }

    public class SignUpRequest extends AsyncTask<Void, Void, String> {

        private final OnSignUpListener listener;
        private final String login;
        private final String password;
        private final String name;

        public SignUpRequest(OnSignUpListener listener, String login, String name, String password) {
            this.listener = listener;
            this.login = login;
            this.name = name;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            String json = "{\"user\":{ \"login\": \"" + login + "\"," +
                    "\"name\": \"" + name + "\"," +
                    "\"password\": \"" + password + "\", " +
                    "\"themes\": \"\",\"history\": \"\" }}";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((MAIN_URL + "api/users/")).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response_str;
        }

        @Override
        protected void onPostExecute(String server_response) {
            try {
                JSONObject obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    listener.onClick(R.string.user_created_successful);
                    return;
                } else if (obj.getString("status").equals("bad response")) {
                    listener.onClick(obj.getString("trouble"));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onClick(R.string.trouble_with_autarisation);
        }

        String deleteTheme(String fullStr, String deletingStr) {
            String outStr = "";
            for (int i = 0; i < fullStr.length(); i++) {
                if (fullStr.charAt(i) == deletingStr.charAt(0))
                    while (fullStr.charAt(i) != 0) i++;
                outStr += fullStr.charAt(i);
            }
            return fullStr;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadUser extends AsyncTask<Void, Void, String> {

        private final OnLoadUserListener mListener;
        private final String login;
        private final String password;

        public LoadUser(String login, String password, OnLoadUserListener mListener) {
            this.login = login;
            this.password = password;
            this.mListener = mListener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            final String[] response_str = new String[1];
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{ \"login\": \"" + login + "\",\"password\": \"" + password + "\"}}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((MAIN_URL + "api/user_check/")).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str[0] = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response_str[0];

        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("response", response);
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                try {
                    if (obj.getString("status").equals("ok")) {
                        try {
                            User user = User.serializeUser(obj.getString("user"));
                            mListener.onResults(user);
                            /*
                            fragment_set.progressBar.setVisibility(View.INVISIBLE);
                            fragment_set.layout_success.setVisibility(View.VISIBLE);
                            fragment_set.change_fields(user);*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class FindNews extends AsyncTask<Void, Void, String> {

        private final OnFindNewsListener findNewsListener;
        private final String theme;
        private User user;

        public FindNews(String theme, User user, OnFindNewsListener findNewsListener) {
            this.theme = theme;
            this.user = user;
            this.findNewsListener = findNewsListener;
        }

        @Override
        protected void onPostExecute(String s) {

            String status = "";
            JSONObject obj = null;
            try {
                obj = new JSONObject(s);
                status = obj.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equals("ok")) {
                try {
                    ArrayList<News> newsList = News.serializeNews(obj.getString("news"));
                    if (newsList.size() != 0) {
                        findNewsListener.onFoundNews(newsList);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            findNewsListener.onFoundNews(null);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            user = updateUser(user);

            Request request = new Request.Builder().url((MAIN_URL + "api/news/" + theme + "/" +
                    user.getId())).get().build();
            Log.d("TAG", "doInBackground: " + (MAIN_URL + "api/news/" + theme + "/" +
                    user.getId()));
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
                if (new JSONObject(response_str).getString("status").equals("ok")) {
                    final MediaType JSON = MediaType.get("application/json; charset=utf-8");

                    client = new OkHttpClient();

                    String json = "{\"user\":{" + "\"history\": \"" +
                            user.getHistory() + ";" + theme + "\" }}";

                    RequestBody body = RequestBody.create(json, JSON);
                    request = new Request.Builder().url((MAIN_URL + "api/users/" +
                            user.getId())).put(body).build();
                    try (Response response1 = client.newCall(request).execute()) {
                        String response_str1 = response1.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return response_str;
        }
    }

    public class FindTopNews extends AsyncTask<Void, Void, String> {

        OnFindTopNewsListener findTopNewsListener;
        ArrayList<News> newsList = new ArrayList<>();

        public FindTopNews(OnFindTopNewsListener findTopNewsListener) {
            this.findTopNewsListener = findTopNewsListener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String responseStr = "";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url((MAIN_URL + "api/top_news/")).get().build();
            try (Response response = client.newCall(request).execute()) {
                responseStr = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseStr;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                String status = obj.getString("status");
                if (status.equals("ok")) {
                    try {
                        newsList = News.serializeNews(obj.getString("news"));
                        if (newsList.size() != 0) {
                            findTopNewsListener.onFind(newsList);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            findTopNewsListener.onFind(null);
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateUserAsync extends AsyncTask<Void, Void, String> {

        private final OnUserChangedListener listener;
        private final User user;

        public UpdateUserAsync(OnUserChangedListener listener, User user) {
            this.listener = listener;
            this.user = user;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            String json =
                    "{\"user\":{"
                            + "\"login\": \"" + user.getLogin() + "\" ,"
                            + "\"password\": \"" + user.getPassword() + "\" ,"
                            + "\"name\": \"" + user.getName() + "\" ,"
                            + "\"history\": \"" + user.getHistory() + "\" ,"
                            + "\"sites\": \"" + user.getSites() + "\" ,"
                            + "\"themes\": \"" + user.getThemes() + "\""
                            + "}}";

            Log.d("json", json);

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((MAIN_URL + "api/users/" + user.getId())).put(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("asd", response_str);
            return response_str;
        }

        @Override
        protected void onPostExecute(String server_response) {
            try {
                JSONObject obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    listener.onChanged(server_response);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onChanged(null);
        }

    }

    User updateUser(User user) {
        String json = "{\"user\":{ \"login\": \"" + user.getLogin() + "\",\"password\": \"" + user.getPassword() + "\"}}";
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        final String[] response_str = new String[1];

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url((MAIN_URL + "api/user_check/")).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            response_str[0] = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return User.serializeUser(new JSONObject(response_str[0]).getString("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public interface OnSignUpListener {
        void onClick(String response);

        void onClick(int responseId);
    }

    public interface OnSingInListener {
        void onSingIn(User user);
    }

    public interface OnLoadUserListener {
        void onResults(User user);
    }

    public interface OnFindNewsListener {
        void onFoundNews(ArrayList<News> listNews);

    }

    public interface OnFindTopNewsListener {
        void onFind(ArrayList<News> listNews);
    }

    public interface OnUserChangedListener {
        void onChanged(String serverResponse);

    }

}
