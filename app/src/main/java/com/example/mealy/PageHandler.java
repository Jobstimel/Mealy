package com.example.mealy;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;

public class PageHandler {

    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;
    private LinearLayout mTutorial;
    private LinearLayout mLoadScreen;
    private SharedPreferences mSharedPreferences;
    private String mMode;

    public PageHandler(LinearLayout mPage1, LinearLayout mPage2, LinearLayout mPage3, LinearLayout mTutorial, LinearLayout mLoadScreen, SharedPreferences mSharedPreferences, String mode) {
        this.mPage1 = mPage1;
        this.mPage2 = mPage2;
        this.mPage3 = mPage3;
        this.mTutorial = mTutorial;
        this.mLoadScreen = mLoadScreen;
        this.mSharedPreferences = mSharedPreferences;
        this.mMode = mode;
    }

    public void loadCorrectPage() {
        Integer currentPage = mSharedPreferences.getInt("Page"+mMode, 1);
        mLoadScreen.setVisibility(View.GONE);
        if (currentPage == 1) {
            mPage1.setVisibility(View.VISIBLE);
            mPage2.setVisibility(View.GONE);
            mPage3.setVisibility(View.GONE);
            mLoadScreen.setVisibility(View.GONE);
        }
        else if (currentPage == 2) {
            if (!mSharedPreferences.getBoolean(mMode+"Tutorial", false)) {
                mTutorial.setVisibility(View.VISIBLE);
            }
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.VISIBLE);
            mPage3.setVisibility(View.GONE);
        }
        else {
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.GONE);
            mPage3.setVisibility(View.VISIBLE);
        }
    }

    public void showLoadScreen() {
        mPage1.setVisibility(View.GONE);
        mPage2.setVisibility(View.GONE);
        mPage3.setVisibility(View.GONE);
        mTutorial.setVisibility(View.GONE);
        mLoadScreen.setVisibility(View.VISIBLE);
    }

    public void savePage(int page) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("Page"+mMode, page);
        editor.commit();
    }
}
