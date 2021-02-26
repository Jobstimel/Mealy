package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SwipeHandler {

    private String mMode;
    private SharedPreferences mSharedPreferences;

    public List<Integer> mSelectedIDs;
    public List<Integer> mLikedIDs;
    public List<Integer> mDislikedIDs;
    public List<Recipe> mOfflineResults;

    public SwipeHandler(String mMode, SharedPreferences mSharedPreferences) {
        this.mMode = mMode;
        this.mSharedPreferences = mSharedPreferences;
    }

    public void loadSelectedIndices() {
        mSelectedIDs = new ArrayList<>();
        String tmp1 = mSharedPreferences.getString("Selected"+mMode+"IDs", "");
        if (!tmp1.equals("")) {
            String[] tmp2 = tmp1.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mSelectedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }

    public void loadLikedIndices() {
        mLikedIDs = new ArrayList<>();
        String tmp = mSharedPreferences.getString("Liked"+mMode+"IDs", "");
        if (!tmp.equals("")) {
            String[] tmp2 = tmp.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mLikedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }

    public void loadDislikedIndices() {
        mDislikedIDs = new ArrayList<>();
        String tmp = mSharedPreferences.getString("Disliked"+mMode+"IDs", "");
        if (!tmp.equals("")) {
            String[] tmp2 = tmp.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mDislikedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }

    public void saveLikedIndices(List<Integer> liked) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String tmp = "";
        if (liked.size() > 0) {
            tmp = String.valueOf(liked.get(0));
            for (int i = 1; i < liked.size(); i++) {
                tmp = tmp + "#" + liked.get(i);
            }
        }
        editor.putString("Liked"+mMode+"IDs", tmp);
        editor.commit();
    }

    public void saveDislikedIndices(List<Integer> disliked) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String tmp = "";
        if (disliked.size() > 0) {
            tmp = String.valueOf(disliked.get(0));
            for (int i = 1; i < disliked.size(); i++) {
                tmp = tmp + "#" + disliked.get(i);
            }
        }
        editor.putString("Disliked"+mMode+"IDs", tmp);
        editor.commit();
    }

    public void loadOfflineResults(List<Recipe> recipes, List<Integer> liked) {
        mOfflineResults = new ArrayList<>();
        for (int i = 0; i < liked.size(); i++) {
            Recipe recipe = recipes.get(liked.get(i));
            recipe.setScore(1);
            mOfflineResults.add(recipe);
        }
    }
}
