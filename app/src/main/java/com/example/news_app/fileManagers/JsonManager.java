package com.example.news_app.fileManagers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.news_app.models.News;
import com.example.news_app.models.SavedData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class JsonManager {

    private static final String JSON_FILE_NAME = "userData";
    private static final String VERY_IMPORTANT_SEED = "userData";
    private final Context context;
    private final Gson gson;
    private SecretKeySpec sks;

    public JsonManager(Context context) {
        this.context = context;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        sks = stringToKey("a0VPQ/SggW4gBRKRNM000A==");
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public SavedData readUserFromJson() {
        try {
            File file = new File(context.getFilesDir(), JSON_FILE_NAME);
            FileReader fileReader = null;
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            //String responce = Base64.decode(stringBuilder.toString(), 0);
            // This responce will have Json Format String
            //String responce = stringBuilder.toString();

            // Decode the encoded data with AES
            byte[] decodedBytes = null;
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, sks);

                //Log.d("Crypto", stringBuilder.toString());
                //decodedBytes = c.doFinal(responce.getBytes(Charset.forName("UTR-8")));
                decodedBytes = c.doFinal(Base64.decode(stringBuilder.toString(), 0));

            } catch (Exception e) {
                Log.e("Crypto", "AES decryption error");
            }
            if(decodedBytes == null) return null;
            Log.d("TAG", "readUserFromJson: " + new String(decodedBytes));
            return gson.fromJson(new String(decodedBytes), SavedData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean writeOnlyTopNewsToJson (ArrayList<News> listTopNews){

        SavedData savedData = readUserFromJson();
        savedData.setListTopNews(listTopNews);
        writeDataToJson(savedData);

        return false;
    }

    public static SecretKeySpec stringToKey(String stringKey) {
        byte[] encodedKey = Base64.decode(stringKey.trim(), Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

}
