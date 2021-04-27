package com.example.news_app.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.news_app.adapters.AdapterTopNews;
import com.example.news_app.fragments.FragmentSearching;
import com.example.news_app.fragments.FragmentSignIn;
import com.example.news_app.fragments.FragmentSingUp;
import com.example.news_app.fragments.FragmentTopNews;
import com.example.news_app.fragments.FragmentTrackingTheme;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

    public class AddTrackingTheme extends AsyncTask<Void, Void, String> {

        FragmentTrackingTheme fragment;

        public AddTrackingTheme(FragmentTrackingTheme fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{" +
                    "\"themes\": \"" + fragment.user.getThemes() + fragment.addingTheme + ";\"}}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((mainUrl + "api/users/" + fragment.user.getId())).put(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("asd", response_str);
            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragment.binding.progressCircular.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    User user = null;
                    try {
                        user = serializeUser(obj.getString("user"));
                        fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
                        fragment.alertDialog.dismiss();
                        fragment.user = user;
                        fragment.adapter.themes_list = serialiseThemes(user.getThemes().split(";"));
                        fragment.clearThemes();
                        fragment.binding.recyclerView.getAdapter().notifyDataSetChanged();

                        Toast.makeText(fragment.getContext(), "Тема успешно добавлена", Toast.LENGTH_SHORT).show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }
    }

    public class DeleteTheme extends AsyncTask<Void, Void, String> {

        FragmentTrackingTheme fragment;
        String deleting_theme;

        public DeleteTheme(FragmentTrackingTheme fragment, String deleting_theme) {
            this.fragment = fragment;
            this.deleting_theme = deleting_theme;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            fragment.user.setThemes(fragment.user.getThemes().replace(";;", ";"));

            String json = "{\"user\":{" +
                    "\"themes\": \"" + fragment.user.getThemes().replace(deleting_theme + ";", "") + ";\"}}";

            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((mainUrl + "api/users/" + fragment.user.getId())).put(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("asd", response_str);
            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragment.binding.progressCircular.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    User user = null;
                    try {
                        user = serializeUser(obj.getString("user"));

                        try {
                            user.setThemes(user.getThemes().replace(";;", ";"));
                            if (user.getThemes().charAt(0) == ';') {
                                user.setThemes(user.getThemes().substring(1));
                            }
                        } catch (Exception e) {
                        }

                        fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
                        fragment.user = user;
                        fragment.clearThemes();
                        fragment.adapter.themes_list = serialiseThemes(user.getThemes().split(";"));
                        fragment.binding.recyclerView.getAdapter().notifyDataSetChanged();

                        Toast.makeText(fragment.getContext(), "Тема успешно удвлена", Toast.LENGTH_SHORT).show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }
    }

    public class SignInRequest extends AsyncTask<Void, Void, String> {

        FragmentSignIn fragment;
        boolean isFromUser;

        public SignInRequest(FragmentSignIn fragment) {
            this.fragment = fragment;
        }

        public SignInRequest(FragmentSignIn fragment, boolean isFromUser) {
            this.fragment = fragment;
            this.isFromUser = isFromUser;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{ \"login\": \"" + fragment.login + "\",\"password\": \"" + fragment.password + "\"}}";
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
        protected void onPreExecute() {
            fragment.binding.progressCircular.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {

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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Возникла проблема с автоматической авторизацией", Toast.LENGTH_LONG).show();
        }
    }

    public class SignUpRequest extends AsyncTask<Void, Void, String> {

        FragmentSingUp fragment;

        public SignUpRequest(FragmentSingUp fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{ \"login\": \"" + fragment.login + "\"," +
                    "\"name\": \"" + fragment.name + "\"," +
                    "\"password\": \"" + fragment.password + "\", " +
                    "\"themes\": \"\",\"history\": \"\" }}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((mainUrl + "api/users/")).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragment.binding.progressCircular.setVisibility(View.VISIBLE);
            fragment.binding.btnSignUp.setEnabled(false);
            fragment.binding.btnBack.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            fragment.binding.btnSignUp.setEnabled(true);
            fragment.binding.btnBack.setEnabled(true);
            fragment.binding.progressCircular.setVisibility(View.INVISIBLE);
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    Toast.makeText(fragment.getActivity(), "Пользователь создан успешно", Toast.LENGTH_SHORT).show();
                    return;
                } else if (obj.getString("status").equals("bad response")) {
                    Toast.makeText(fragment.getActivity(), obj.getString("trouble"), Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(fragment.getActivity(), "Возникла проблема с автоматической авторизацией", Toast.LENGTH_LONG).show();
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

    /*
    class LoadUser extends AsyncTask<Void, Void, String> {

        Settings fragment_set;

        LoadUser(Settings fragment_set) {
            this.fragment_set = fragment_set;
        }

        @Override
        protected String doInBackground(Void... voids) {
            final String[] response_str = new String[1];
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{ \"login\": \"" + fragment_set.user.login + "\",\"password\": \"" + fragment_set.user.password + "\"}}";
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
        protected void onPreExecute() {
            fragment_set.layout_success.setVisibility(View.INVISIBLE);
            fragment_set.layout_error.setVisibility(View.INVISIBLE);
            fragment_set.progressBar.setVisibility(View.VISIBLE);
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
                            User user = serialize_User(obj.getString("user"));
                            fragment_set.progressBar.setVisibility(View.INVISIBLE);
                            fragment_set.layout_success.setVisibility(View.VISIBLE);
                            fragment_set.change_fields(user);
                            return;
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
            fragment_set.progressBar.setVisibility(View.INVISIBLE);
            fragment_set.layout_error.setVisibility(View.VISIBLE);
        }
    }*/

    public class FindNews extends AsyncTask<Void, Void, String> {

        String theme;

        final String[] status = {""};
        ArrayList<News> news_list = new ArrayList<>();
        FragmentSearching fragment_Fragment_searching;

        public FindNews(FragmentSearching fragment_Fragment_searching, String theme) {
            this.fragment_Fragment_searching = fragment_Fragment_searching;
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
                        fragment_Fragment_searching.adapter.setNewsArray(news_list);
                        fragment_Fragment_searching.binding.viewPager.setAdapter(fragment_Fragment_searching.adapter);
                        fragment_Fragment_searching.binding.viewPager.getAdapter().notifyDataSetChanged();

                        fragment_Fragment_searching.binding.progressCircular.setVisibility(View.INVISIBLE);
                        fragment_Fragment_searching.binding.viewPager.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            fragment_Fragment_searching.binding.errorLayout.setVisibility(View.VISIBLE);
            fragment_Fragment_searching.binding.progressCircular.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            OkHttpClient client = new OkHttpClient();
            fragment_Fragment_searching.user = updateUser(fragment_Fragment_searching.user);

            Request request = new Request.Builder().url((mainUrl + "api/news/" + theme + "/")).get().build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
                Log.d("asd", response_str);
                if (new JSONObject(response_str).getString("status").equals("ok")) {
                    final MediaType JSON = MediaType.get("application/json; charset=utf-8");

                    client = new OkHttpClient();

                    String json = "{\"user\":{" +
                            "\"history\": \"" + fragment_Fragment_searching.user.getHistory() + ";" + theme + "\" }}";
                    Log.d("json", json);

                    RequestBody body = RequestBody.create(json, JSON);
                    request = new Request.Builder().url((mainUrl + "api/users/" + fragment_Fragment_searching.user.getId())).put(body).build();
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
            fragment_Fragment_searching.binding.imgHello.setVisibility(View.INVISIBLE);
            fragment_Fragment_searching.binding.viewPager.setVisibility(View.INVISIBLE);
            fragment_Fragment_searching.binding.errorLayout.setVisibility(View.INVISIBLE);
            fragment_Fragment_searching.binding.progressCircular.setVisibility(View.VISIBLE);
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
        };
        return user;
    }

}
