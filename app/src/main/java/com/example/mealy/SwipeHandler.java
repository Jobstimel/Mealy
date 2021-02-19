package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SwipeHandler {

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public List<Integer> mSelectedIDs;

    public SwipeHandler(String mMode, SharedPreferences mSharedPreferences, Context mContext) {
        this.mMode = mMode;
        this.mSharedPreferences = mSharedPreferences;
        this.mContext = mContext;
    }

    public void loadSelectedIndices() {
        mSelectedIDs = new ArrayList<>();
        String tmp1 = mSharedPreferences.getString("Selected"+mMode+"IDs", "");
        Log.d("ONRESUME", "Debug 4: "+ tmp1);
        if (!tmp1.equals("")) {
            String[] tmp2 = tmp1.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mSelectedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }
}
