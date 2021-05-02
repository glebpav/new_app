package com.example.news_app.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.news_app.R;
import com.example.news_app.adapters.AdapterTopNews;
import com.example.news_app.fragments.usualFragments.FragmentSearching;
import com.example.news_app.fragments.usualFragments.FragmentTopNews;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeRequests {

    String mainUrl;

    public MakeRequests(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public void changeUser(final User user) {
        final String[] responseStr = new String[1];
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Thread t1 = new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String json = "{\"user\":{" +
                        "\"name\": \"" + user.getName() + "\"}}";
                Log.d("json", json);

                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder().url((mainUrl + "api/users/" + user.getId())).put(body).build();
                try (Response response = client.newCall(request).execute()) {
                    responseStr[0] = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        try {
            t1.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class AddTrackingTheme extends AsyncTask<Void, Void, String> {

        private OnAddTrackingThemeListener listener;
        private String addingTheme;
        private User user;

        public AddTrackingTheme(OnAddTrackingThemeListener listener, User user, String addingTheme) {
            this.listener = listener;
            this.user = user;
            this.addingTheme = addingTheme;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            String json = "{\"user\":{" + "\"themes\": \"" + user.getThemes() + addingTheme + ";\"}}";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((mainUrl + "api/users/" + user.getId())).put(body).build();
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
                    try {
                        User user = serializeUser(obj.getString("user"));
                        listener.onClick(user);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onClick(null);
            //fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
            //Toast.makeText(fragment.getActivity(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DeleteTheme extends AsyncTask<Void, Void, String> {

        String deletingTheme;
        OnAddTrackingThemeListener deleteTrackingThemeListener;
        User user;

        public DeleteTheme(String deletingTheme, User user, OnAddTrackingThemeListener deleteTrackingThemeListener) {
            this.deletingTheme = deletingTheme;
            this.user = user;
            this.deleteTrackingThemeListener = deleteTrackingThemeListener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            user.setThemes(user.getThemes().replace(";;", ";"));
            String json = "{\"user\":{" +
                    "\"themes\": \"" + user.getThemes().replace(deletingTheme + ";", "") + ";\"}}";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((mainUrl + "api/users/" + user.getId())).put(body).build();
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
                    try {
                        User user = serializeUser(obj.getString("user"));
                        deleteTrackingThemeListener.onClick(user);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            deleteTrackingThemeListener.onClick(null);
        }
    }

    public class SignInRequest extends AsyncTask<Void, Void, String> {

        String login, password;
        OnSingInListener listener;

        public SignInRequest(String login, String password, OnSingInListener listener) {
            this.login = login;
            this.password = password;
            this.listener = listener;
        }

        public SignInRequest(){};

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{ \"login\": \"" + login + "\",\"password\": \"" + password + "\"}}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((mainUrl + "api/user_check/")).post(body).build();
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
                    User user = serializeUser(obj.getString("user"));
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
            Request request = new Request.Builder().url((mainUrl + "api/users/")).post(body).build();
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
            listener.onClick(R.string.touble_with_autarisation);
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
            Request request = new Request.Builder().url((mainUrl + "api/user_check/")).post(body).build();
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
                            User user = serializeUser(obj.getString("user"));
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

    public class FindNews extends AsyncTask<Void, Void, String> {

        String theme;

        final String[] status = {""};
        ArrayList<News> news_list = new ArrayList<>();
        FragmentSearching fragmentSearching;

        public FindNews(FragmentSearching fragmentSearching, String theme) {
            this.fragmentSearching = fragmentSearching;
            this.theme = theme;
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

                    news_list = serializeNews(obj.getString("news"));

                    if (news_list.size() != 0) {
                        fragmentSearching.getAdapter().setNewsArray(news_list);
                        fragmentSearching.getBinding().viewPager.setAdapter(fragmentSearching.getAdapter());
                        fragmentSearching.getBinding().viewPager.getAdapter().notifyDataSetChanged();

                        fragmentSearching.getBinding().progressCircular.setVisibility(View.INVISIBLE);
                        fragmentSearching.getBinding().viewPager.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            fragmentSearching.getBinding().errorLayout.setVisibility(View.VISIBLE);
            fragmentSearching.getBinding().progressCircular.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            OkHttpClient client = new OkHttpClient();

            fragmentSearching.setUser(updateUser(fragmentSearching.getUser()));

            Request request = new Request.Builder().url((mainUrl + "api/news/" + theme + "/")).get().build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
                Log.d("asd", response_str);
                if (new JSONObject(response_str).getString("status").equals("ok")) {
                    final MediaType JSON = MediaType.get("application/json; charset=utf-8");

                    client = new OkHttpClient();

                    String json = "{\"user\":{" +
                            "\"history\": \"" + fragmentSearching.getUser().getHistory() + ";" + theme + "\" }}";
                    Log.d("json", json);

                    RequestBody body = RequestBody.create(json, JSON);
                    request = new Request.Builder().url((mainUrl + "api/users/" + fragmentSearching.getUser().getId())).put(body).build();
                    try (Response response1 = client.newCall(request).execute()) {
                        String response_str1 = response1.body().string();
                        Log.d("asd", response_str1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragmentSearching.getBinding().imgHello.setVisibility(View.INVISIBLE);
            fragmentSearching.getBinding().viewPager.setVisibility(View.INVISIBLE);
            fragmentSearching.getBinding().errorLayout.setVisibility(View.INVISIBLE);
            fragmentSearching.getBinding().progressCircular.setVisibility(View.VISIBLE);
        }
    }

    public class FindTopNews extends AsyncTask<Void, Void, String> {

        final String[] status = {""};
        ArrayList<News> news_list = new ArrayList<>();
        FragmentTopNews fragment;

        public FindTopNews(FragmentTopNews fragment) {
            this.fragment = fragment;
        }


        @Override
        protected void onPostExecute(String s) {

            String status = "";
            JSONObject obj = null;
            Log.d("response", s);

            try {
                obj = new JSONObject(s);
                status = obj.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (status.equals("ok")) {
                try {
                    news_list = serializeNews(obj.getString("news"));

                    if (news_list.size() != 0) {
                        fragment.adapter = new AdapterTopNews(fragment.getContext(), news_list);
                        fragment.binding.viewPager.setAdapter(fragment.adapter);
                        //fragment.view_pager.getAdapter().notifyDataSetChanged();
                        fragment.binding.viewPager.setPadding(65, 0, 65, 0);
                        fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
                        fragment.binding.viewPager.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            fragment.binding.layoutError.setVisibility(View.VISIBLE);
            fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url((mainUrl + "api/top_news/")).get().build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
                Log.d("asd", response_str);
                if (new JSONObject(response_str).getString("status").equals("ok")) {
                    final MediaType JSON = MediaType.get("application/json; charset=utf-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragment.binding.viewPager.setVisibility(View.INVISIBLE);
            fragment.binding.layoutError.setVisibility(View.INVISIBLE);
            fragment.binding.progressCircular.setVisibility(View.VISIBLE);
        }
    }

    User serializeUser(String jsonString) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
            return new User(Integer.parseInt(obj.getString("id")), obj.getString("name"),
                    obj.getString("login"), obj.getString("password"),
                    obj.getString("history"), obj.getString("themes"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<News> serializeNews(String news) {
        ArrayList<News> news_list = new ArrayList<>();
        JSONObject obj = null;
        try {
            //obj = new JSONObject(news);
            JSONArray news_array = new JSONArray(news);
            for (int i = 0; i < news_array.length(); i++) {
                JSONObject obj1 = (JSONObject) news_array.get(i);
                News news1 = new News(
                        obj1.getString("title"),
                        obj1.getString("body"),
                        obj1.getString("url"),
                        obj1.getString("rating")
                );
                news_list.add(news1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return news_list;
    }

    ArrayList<String> serialiseThemes(String[] str) {
        ArrayList<String> strings_list = new ArrayList<>();
        for (String str_one : str) {
            strings_list.add(str_one);
        }
        return strings_list;
    }

    User updateUser(User user) {
        final String[] response_str = new String[1];
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String json = "{\"user\":{ \"login\": \"" + user.getLogin() + "\",\"password\": \"" + user.getPassword() + "\"}}";
        Log.d("json", json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url((mainUrl + "api/user_check/")).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            response_str[0] = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return user = serializeUser(new JSONObject(response_str[0]).getString("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ;
        return user;
    }

    public interface OnLoadUserListener {
        void onResults(User user);
    }

    public interface OnAddTrackingThemeListener {
        void onClick(User user);
    }

    public interface OnSignUpListener {
        void onClick(String response);
        void onClick(int responseId);
    }

    public interface OnSingInListener{
        void onSingIn (User user);
    }


}
