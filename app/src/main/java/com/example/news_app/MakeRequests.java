package com.example.news_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    String main_url;

    MakeRequests(String main_url) {
        this.main_url = main_url;
    }

    String sign_in(final String login, final String password) {
        final String[] response_str = new String[1];
        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");
        Thread t1 = new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String json = "{\"user\":{ \"login\": \"" + login + "\",\"password\": \"" + password + "\"}}";
                Log.d("json", json);

                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder().url((main_url + "api/user_check/")).post(body).build();
                try (Response response = client.newCall(request).execute()) {
                    response_str[0] = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        try {
            t1.join();
            return response_str[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response_str[0];
    }

    String sign_up(final String login, final String password, final String name) {
        final String[] response_str = new String[1];
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Thread t1 = new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String json = "{\"user\":{ \"login\": \"" + login + "\"," +
                        "\"name\": \"" + name + "\"," +
                        "\"password\": \"" + password + "\", " +
                        "\"themes\": \"null\",\"history\": \"null\" }}";
                Log.d("json", json);

                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder().url((main_url + "api/users/")).post(body).build();
                try (Response response = client.newCall(request).execute()) {
                    response_str[0] = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        try {
            t1.join();
            Log.d("response", response_str[0]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response_str[0];
    }

    String change_user(final User user) {
        final String[] response_str = new String[1];
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");

        Thread t1 = new Thread() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String json = "{\"user\":{" +
                        "\"name\": \"" + user.name + "\"}}";
                Log.d("json", json);

                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder().url((main_url + "api/users/" + user.id)).put(body).build();
                try (Response response = client.newCall(request).execute()) {
                    response_str[0] = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        try {
            t1.join();
            Log.d("response", response_str[0]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response_str[0];
    }

    class Add_tracking_theme extends AsyncTask<Void, Void, String> {

        Tracking fragment;

        Add_tracking_theme(Tracking fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{" +
                    "\"themes\": \"" + fragment.user.themes+ fragment.adding_theme +";\"}}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((main_url + "api/users/" + fragment.user.id)).put(body).build();
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
            fragment.progress_bar_alert.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    User user = null;
                    try {
                        user = serialize_User(obj.getString("user"));
                        fragment.progress_bar_alert.setVisibility(View.INVISIBLE);
                        fragment.alertDialog.dismiss();
                        fragment.user = user;
                        fragment.adapter.themes_list = serialise_themes(user.themes.split(";"));
                        fragment.recycler_view.getAdapter().notifyDataSetChanged();

                        Toast.makeText(fragment.getContext(), "Тема успешно добавлена", Toast.LENGTH_SHORT).show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.progress_bar_alert.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }
    }

    class Delete_theme extends AsyncTask<Void, Void, String> {

        Tracking fragment;
        String deleting_theme;

        Delete_theme(Tracking fragment, String deleting_theme) {
            this.fragment = fragment;
            this.deleting_theme = deleting_theme;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{" +
                    "\"themes\": \"" + fragment.user.themes.replace(deleting_theme+";", "") +";\"}}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((main_url + "api/users/" + fragment.user.id)).put(body).build();
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
            fragment.progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    User user = null;
                    try {
                        user = serialize_User(obj.getString("user"));
                        fragment.progress_bar.setVisibility(View.INVISIBLE);
                        fragment.user = user;
                        fragment.adapter.themes_list = serialise_themes(user.themes.split(";"));
                        fragment.recycler_view.getAdapter().notifyDataSetChanged();

                        Toast.makeText(fragment.getContext(), "Тема успешно удвлена", Toast.LENGTH_SHORT).show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.progress_bar_alert.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        }
    }

    class Sign_in_request extends AsyncTask<Void, Void, String> {

        Sign_in fragment;

        Sign_in_request(Sign_in fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();

            String json = "{\"user\":{ \"login\": \"" + fragment.login + "\",\"password\": \"" + fragment.password + "\"}}";
            Log.d("json", json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((main_url + "api/user_check/")).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragment.progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    User user = null;
                    try {
                        user = serialize_User(obj.getString("user"));
                        fragment.goto_next_activity(user);
                        fragment.progress_bar.setVisibility(View.INVISIBLE);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fragment.progress_bar.setVisibility(View.INVISIBLE);
            Toast.makeText(fragment.getActivity(), "Возникла проблема с автоматической авторизацией", Toast.LENGTH_LONG).show();
        }
    }

    class Sign_up_request extends AsyncTask<Void, Void, String> {

        Sing_up fragment;

        Sign_up_request(Sing_up fragment) {
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
            Request request = new Request.Builder().url((main_url + "api/users/")).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response_str;
        }

        @Override
        protected void onPreExecute() {
            fragment.progress_bar.setVisibility(View.VISIBLE);
            fragment.btn_sing_up.setEnabled(false);
            fragment.btn_back.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String server_response) {
            JSONObject obj = null;
            fragment.btn_sing_up.setEnabled(true);
            fragment.btn_back.setEnabled(true);
            fragment.progress_bar.setVisibility(View.INVISIBLE);
            try {
                obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    Toast.makeText(fragment.getActivity(), "Пользователь создан успешно", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (obj.getString("status").equals("bad response")){
                    Toast.makeText(fragment.getActivity(), obj.getString("trouble"), Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(fragment.getActivity(), "Возникла проблема с автоматической авторизацией", Toast.LENGTH_LONG).show();
        }
    }

    class Load_user extends AsyncTask<Void, Void, String> {

        Settings fragment_set;

        Load_user(Settings fragment_set) {
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
            Request request = new Request.Builder().url((main_url + "api/user_check/")).post(body).build();
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
    }

    class Find_news extends AsyncTask<Void, Void, String> {

        String theme;

        final String[] status = {""};
        ArrayList<News> news_list = new ArrayList<>();
        Searching fragment_searching;

        public Find_news(Searching fragment_searching, String theme) {
            this.fragment_searching = fragment_searching;
            this.theme = theme;
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

                    news_list = serialize_news(obj.getString("news"));

                    if (news_list.size() != 0) {
                        fragment_searching.adapter.news_array = news_list;
                        fragment_searching.view_pager.setAdapter(fragment_searching.adapter);
                        fragment_searching.view_pager.getAdapter().notifyDataSetChanged();

                        fragment_searching.progressBar.setVisibility(View.INVISIBLE);
                        fragment_searching.view_pager.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            fragment_searching.layout_error.setVisibility(View.VISIBLE);
            fragment_searching.progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            OkHttpClient client = new OkHttpClient();
            fragment_searching.user = update_user(fragment_searching.user);

            Request request = new Request.Builder().url((main_url + "api/news/" + theme + "/")).get().build();
            try (Response response = client.newCall(request).execute()) {
                response_str = response.body().string();
                Log.d("asd", response_str);
                if (new JSONObject(response_str).getString("status").equals("ok")) {
                    final MediaType JSON = MediaType.get("application/json; charset=utf-8");

                    client = new OkHttpClient();

                    String json = "{\"user\":{" +
                            "\"history\": \"" + fragment_searching.user.history + ";" + theme + "\" }}";
                    Log.d("json", json);

                    RequestBody body = RequestBody.create(json, JSON);
                    request = new Request.Builder().url((main_url + "api/users/" + fragment_searching.user.id)).put(body).build();
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
            fragment_searching.image_view.setVisibility(View.INVISIBLE);
            fragment_searching.view_pager.setVisibility(View.INVISIBLE);
            fragment_searching.layout_error.setVisibility(View.INVISIBLE);
            fragment_searching.progressBar.setVisibility(View.VISIBLE);
        }
    }

    class Find_top_news extends AsyncTask<Void, Void, String> {

        final String[] status = {""};
        ArrayList<News> news_list = new ArrayList<>();
        Top_News fragment;

        public Find_top_news(Top_News fragment) {
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
                    news_list = serialize_news(obj.getString("news"));

                    if (news_list.size() != 0) {
                        fragment.adapter = new Top_news_adapter(fragment.getContext(), news_list);
                        fragment.view_pager.setAdapter(fragment.adapter);
                        //fragment.view_pager.getAdapter().notifyDataSetChanged();
                        fragment.view_pager.setPadding(65,0,65,0);
                        fragment.progressBar.setVisibility(View.INVISIBLE);
                        fragment.view_pager.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            fragment.layout_error.setVisibility(View.VISIBLE);
            fragment.progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response_str = "";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url((main_url + "api/top_news/")).get().build();
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
            fragment.view_pager.setVisibility(View.INVISIBLE);
            fragment.layout_error.setVisibility(View.INVISIBLE);
            fragment.progressBar.setVisibility(View.VISIBLE);
        }
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

    ArrayList<News> serialize_news(String news) {
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

    ArrayList<String> serialise_themes(String[] str) {
        ArrayList<String> strings_list = new ArrayList<>();
        for (String str_one : str) {
            strings_list.add(str_one);
        }
        return strings_list;
    }

    User update_user(User user) {
        final String[] response_str = new String[1];
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String json = "{\"user\":{ \"login\": \"" + user.login + "\",\"password\": \"" + user.password + "\"}}";
        Log.d("json", json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url((main_url + "api/user_check/")).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            response_str[0] = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Log.d("asd", "success");
            return user = serialize_User(new JSONObject(response_str[0]).getString("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ;
        return user;
    }

}
