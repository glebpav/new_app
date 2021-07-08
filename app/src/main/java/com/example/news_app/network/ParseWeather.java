package com.example.news_app.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperColors;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.news_app.models.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParseWeather extends AsyncTask<Void, Void, String> {

    private static final String OPEN_WEATHER_API_URL_1 = "https://api.openweathermap.org/data/2.5/weather?lat=";
    private static final String OPEN_WEATHER_API_URL_2 = "&lon=";
    private static final String OPEN_WEATHER_API_URL_3 = "&units=metric&lang=ru&appid=591bd21525458ba0815a231896fe4e99";

    private static final String OPEN_WEATHER_ICON_URL = "https://openweathermap.org/img/wn/{id}@2x.png";

    private static final String TAG = "PARSE_WEATHER_SPACE";
    private static final int PERMISSION_ID = 44;

    private volatile boolean flag;
    private OnFindWeatherListener weatherListener;

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private FusedLocationProviderClient mFusedLocationClient;
    private String latitude;
    private String longitude;

    public ParseWeather(Context mContext, OnFindWeatherListener weatherListener) {
        this.mContext = mContext;
        this.weatherListener = weatherListener;
        flag = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLocation();
    }

    public ParseWeather(Context mContext){
        this.mContext = mContext;
        flag = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                        Log.d(TAG, "lat : " + latitude);
                        Log.d(TAG, "lon : " + longitude);
                        flag = true;
                    }
                });
            } else {
                Toast.makeText(mContext, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        } else {
            // if permissions aren't available, request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected String doInBackground(Void... voids) {
        String responseStr = "";
        OkHttpClient client = new OkHttpClient();

        while(!flag){}

        Log.d(TAG, "doInBackground: " + OPEN_WEATHER_API_URL_1 + latitude
                + OPEN_WEATHER_API_URL_2 + longitude
                + OPEN_WEATHER_API_URL_3);

        Request request = new Request.Builder().url(
                OPEN_WEATHER_API_URL_1 + latitude
                        + OPEN_WEATHER_API_URL_2 + longitude
                        + OPEN_WEATHER_API_URL_3).get().build();
        try (Response response = client.newCall(request).execute()) {
            responseStr = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseStr;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject jsonObject1 = jsonObject.getJSONObject("main");
            JSONArray jsonObject2 = jsonObject.getJSONArray("weather");
            JSONObject jsonObject3 = (JSONObject) jsonObject2.get(0);

            String temperature = jsonObject1.getString("temp");
            String weatherDesc = jsonObject3.getString("description");
            String iconUrl = OPEN_WEATHER_ICON_URL.replace("{id}", jsonObject3.getString("icon"));

            Log.d(TAG, "temperature " + temperature);
            Log.d(TAG, "desc " + weatherDesc);
            Log.d(TAG, "iconUrl " + iconUrl);

            weatherListener.OnFind(new Weather(temperature, weatherDesc, iconUrl));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "weatherErr " + s);
    }

    public interface OnFindWeatherListener{
        void OnFind(Weather weather);
    }
}
