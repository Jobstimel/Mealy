package com.example.mealy;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonLoader {

    public static List<Recipe> loadRecipies(Context context) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONArray jsonArray = new JSONArray(loadJsonFromAsset(context, "data.json"));
            ArrayList<Recipe> recipes = new ArrayList<Recipe>();
            for (int i = 0; i < jsonArray.length(); i++) {
                recipes.add(gson.fromJson(jsonArray.getString(i), Recipe.class));
            }
            return recipes;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String loadJsonFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream inputStream = null;
        try {
            AssetManager assetManager = context.getAssets();
            Log.d("Loader", "path "+jsonFileName);
            inputStream = assetManager.open(jsonFileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
