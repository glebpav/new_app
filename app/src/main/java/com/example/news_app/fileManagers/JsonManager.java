package com.example.news_app.fileManagers;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.news_app.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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

        sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(VERY_IMPORTANT_SEED.getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
        }

    }

    public boolean writeUserToJson(User user) {
        try {
            String userString = gson.toJson(user);
            File file = new File(context.getFilesDir(), JSON_FILE_NAME);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(userString);
            bufferedWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User readUserFromJson() {
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

            // Decode the encoded data with AES
            byte[] decodedBytes = null;
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, sks);
                decodedBytes = c.doFinal(Base64.decode(stringBuilder.toString(), 0));

            } catch (Exception e) {
                Log.e("Crypto", "AES decryption error");
            }

            return gson.fromJson(new String(decodedBytes), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
