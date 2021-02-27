package com.example.mealy;

import android.content.SharedPreferences;

import java.security.SecureRandom;

public class RandomGenerator {

    private SharedPreferences mSharedPreferences;
    static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final String numbs = "0123456789";
    static final Integer GROUP_CODE_LENGTH = 8;
    static SecureRandom random = new SecureRandom();

    public RandomGenerator(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public void generateRandomUserID(int length){
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

    public void generateRandomGroupCode() {
        StringBuilder sb = new StringBuilder(GROUP_CODE_LENGTH);
        for(int i = 0; i < GROUP_CODE_LENGTH; i++)
            sb.append(numbs.charAt(random.nextInt(numbs.length())));
        saveGroupCode(sb.toString());
    }

    private void saveGroupCode(String code) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("GroupCode", code);
        editor.commit();
    }
}
