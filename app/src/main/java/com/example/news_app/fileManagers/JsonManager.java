package com.example.news_app.fileManagers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.news_app.models.News;
import com.example.news_app.models.SavedData;
import com.example.news_app.models.Weather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class JsonManager {

    private static final String TAG = "JSON_MANAGER_SPACE";

    private static final String JSON_FILE_NAME = "userData.txt";
    private static final String FILE_DATE_OF_SAVE_NAME = "saveDate.txt";
    private static final String FILE_WEATHER_NAME = "saveWeather.txt";

    private static final String VERY_IMPORTANT_SEED = "a0VPQ/SggW4gBRKRNM000A==";
    private final Context context;
    private final Gson gson;
    private SecretKeySpec sks;

    public JsonManager(Context context) {
        this.context = context;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        sks = stringToKey(VERY_IMPORTANT_SEED);
        //sks = null;
        /*try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(VERY_IMPORTANT_SEED.getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
        }*/

    }

    public boolean writeDataToJson(SavedData data) {
        try {
            //String userString = inputObj.toString();

            byte[] encodedBytes = null;
            try {
                @SuppressLint("GetInstance") Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE, sks);
                encodedBytes = c.doFinal(gson.toJson(data).getBytes());
            } catch (Exception e) {
                Log.e("Crypto", "AES encryption error");
            }

            File file = new File(context.getFilesDir(), JSON_FILE_NAME);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(Base64.encodeToString(encodedBytes, Base64.DEFAULT));
            bufferedWriter.close();

            writeSavingDate();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void writeSavingDate(){
        try {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm dd.MM", Locale.getDefault());
            String timeText = timeFormat.format(new Date());
            Log.d(TAG, "save time : " + timeText);

            File file = new File(context.getFilesDir(), FILE_DATE_OF_SAVE_NAME);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(timeText);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSavingWeather(Weather weather){
        try {
            File file = new File(context.getFilesDir(), FILE_WEATHER_NAME);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(gson.toJson(weather));
            bufferedWriter.close();
            writeSavingDate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SavedData readUserFromJson() {
        try {
            File file = new File(context.getFilesDir(), JSON_FILE_NAME);
            FileReader fileReader;
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            byte[] decodedBytes = null;
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, sks);
                decodedBytes = c.doFinal(Base64.decode(stringBuilder.toString(), 0));

            } catch (Exception e) {
                Log.e("Crypto", "AES decryption error");
            }
            if(decodedBytes == null) return null;

            SavedData savedData = gson.fromJson(new String(decodedBytes), SavedData.class);

            Log.d(TAG, "readUserFromJson: " + new String(decodedBytes));
            return savedData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SavedData();
    }

    public String readSavedDate(){
        try {
            File file = new File(context.getFilesDir(), FILE_DATE_OF_SAVE_NAME);
            FileReader fileReader;
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Weather readSavedWeather(){
        try {
            File file = new File(context.getFilesDir(), FILE_WEATHER_NAME);
            FileReader fileReader;
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            return gson.fromJson(stringBuilder.toString(), Weather.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKeySpec stringToKey(String stringKey) {
        byte[] encodedKey = Base64.decode(stringKey.trim(), Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

}
