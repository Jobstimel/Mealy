package com.example.mealy;

import android.content.SharedPreferences;

import java.security.SecureRandom;

public class UserIDGenerator {

    private SharedPreferences mSharedPreferences;
    static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom random = new SecureRandom();

    public UserIDGenerator(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public void generateRandomID(int length){
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        saveID(sb.toString());
    }

    private void saveID(String id) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("UserID", id);
        editor.commit();
    }
}
