package com.example.news_app.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

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

    final static String TAG = "MAKE_REQUEST_SPACE";
    final static String MAIN_URL = "https://analisinf.pythonanywhere.com/";

    public class SignInRequest extends AsyncTask<Void, Void, String> {

        private final String login;
        private final String password;
        private final OnSingInListener listener;

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

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((MAIN_URL + "api/user_check/")).post(body).build();
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
                Log.d(TAG, "onPostExecute: " + server_response);
                JSONObject obj = new JSONObject(server_response);
                if (obj.getString("status").equals("ok")) {
                    Log.d(TAG, "onPostExecute : " + obj.getString("user"));
                    User user = User.serializeUser(obj.getString("user"));
                    listener.onSingIn(user);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onSingIn(null);
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
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

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
            listener.onClick(R.string.trouble_with_authorization);
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
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("status").equals("ok")) {
                    User user = User.serializeUser(obj.getString("user"));
                    mListener.onResults(user);
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
            try {
                JSONObject obj = new JSONObject(s);
                String status = obj.getString("status");
                if (status.equals("ok")) {
                    ArrayList<News> newsList = News.serializeNews(obj.getString("news"));
                    if (newsList.size() != 0) {
                        findNewsListener.onFoundNews(newsList);
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            findNewsListener.onFoundNews(null);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String responseStr = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            String json1 = "{\"user\":{ \"login\": \"" + user.getLogin() + "\",\"password\": \"" + user.getPassword() + "\"}}";
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            final String[] responseStr1 = new String[1];

            OkHttpClient client1 = new OkHttpClient();
            RequestBody body1 = RequestBody.create(json1, JSON);
            Request request1 = new Request.Builder().url((MAIN_URL + "api/user_check/")).post(body1).build();
            try (Response response = client1.newCall(request1).execute()) {
                responseStr1[0] = Objects.requireNonNull(response.body()).string();
                user = User.serializeUser(new JSONObject(responseStr1[0]).getString("user"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Request request = new Request.Builder().url((MAIN_URL + "api/news/" + theme + "/" +
                    user.getId())).get().build();
            try (Response response = client.newCall(request).execute()) {
                responseStr = response.body().string();
                if (new JSONObject(responseStr).getString("status").equals("ok")) {

                    client = new OkHttpClient();

                    String json = "{\"user\":{" + "\"history\": \"" +
                            user.getHistory() + ";" + theme + "\" }}";

                    RequestBody body = RequestBody.create(json, JSON);
                    request = new Request.Builder().url((MAIN_URL + "api/users/" +
                            user.getId())).put(body).build();
                    try (Response response1 = client.newCall(request).execute()) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return responseStr;
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
                    newsList = News.serializeNews(obj.getString("news"));
                    if (newsList.size() != 0) {
                        findTopNewsListener.onFind(newsList);
                        return;
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
                            + "\"themes\": \"" + user.getThemes() + "\" ,"
                            + "\"currency\": \"" + user.getCurrency() + "\""
                            + "}}";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder().url((MAIN_URL + "api/users/" + user.getId())).put(body).build();
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
                    listener.onChanged(server_response);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onChanged(null);
        }

    }

    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return false;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return false;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo == null || !wifiInfo.isConnected();

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
