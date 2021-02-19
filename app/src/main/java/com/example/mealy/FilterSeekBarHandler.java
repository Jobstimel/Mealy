package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

public class FilterSeekBarHandler {

    private final Integer MAX_CALORIES = 2000;
    private final Integer MAX_TIME = 300;

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public FilterSeekBarHandler(String mMode, SharedPreferences mSharedPreferences, Context mContext) {
        this.mMode = mMode;
        this.mSharedPreferences = mSharedPreferences;
        this.mContext = mContext;
    }

    public void applySeekBar(CrystalRangeSeekbar seekBar, String key, int min, int max) {
        int def = this.MAX_TIME;
        if (key.equals("CaloriesSeekBar")) {
            def = this.MAX_CALORIES;
        }
        if (!(mSharedPreferences.getInt(key+mMode+"Min", 0) == min) || !(mSharedPreferences.getInt(key+mMode+"Max", def) == max)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key+mMode+"Min", min);
            editor.putInt(key+mMode+"Max", max);
            editor.putBoolean("ChangeStatus"+mMode, true);
            editor.commit();
        }
        if (min != 0 || max != def) {
            toggleSeekBarBackgroundColor(seekBar, 1);
        }
        else if (min == 0 && max == def) {
            toggleSeekBarBackgroundColor(seekBar, 0);
        }
    }

    public void loadSeekBarStates(CrystalRangeSeekbar seekBar1, String key1, CrystalRangeSeekbar seekBar2, String key2) {
        loadSeekBarState(seekBar1, key1);
        loadSeekBarState(seekBar2, key2);
    }

    public void resetSeekBarStates(CrystalRangeSeekbar seekBar1, CrystalRangeSeekbar seekBar2) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("CaloriesSeekBar"+mMode+"Min", 0);
        editor.putInt("CaloriesSeekBar"+mMode+"Max", this.MAX_CALORIES);
        editor.putInt("TimeSeekBar"+mMode+"Min", 0);
        editor.putInt("TimeSeekBar"+mMode+"Max", this.MAX_TIME);
        editor.commit();
        toggleSeekBarBackgroundColor(seekBar1, 0);
        toggleSeekBarBackgroundColor(seekBar2, 0);
    }

    private void loadSeekBarState(CrystalRangeSeekbar seekBar, String key) {
        int def = this.MAX_TIME;
        if (key.equals("CaloriesSeekBar")) {
            def = this.MAX_CALORIES;
        }
        seekBar.setMinStartValue(mSharedPreferences.getInt(key+mMode+"Min",0));
        seekBar.setMaxStartValue(mSharedPreferences.getInt(key+mMode+"Max", def));
        seekBar.apply();
        if (mSharedPreferences.getInt(key+mMode+"Min",0) != 0 || mSharedPreferences.getInt(key+mMode+"Max", def) != def) {
            toggleSeekBarBackgroundColor(seekBar, 1);
        }
    }

    private void toggleSeekBarBackgroundColor(CrystalRangeSeekbar seekBar, int state) {
        if (state == 0) {
            seekBar.setLeftThumbColor(ContextCompat.getColor(mContext, R.color.foreground));
            seekBar.setRightThumbColor(ContextCompat.getColor(mContext, R.color.foreground));
            seekBar.setBarHighlightColor(ContextCompat.getColor(mContext, R.color.foreground));
        }
        else {
            seekBar.setLeftThumbColor(ContextCompat.getColor(mContext, R.color.green));
            seekBar.setRightThumbColor(ContextCompat.getColor(mContext, R.color.green));
            seekBar.setBarHighlightColor(ContextCompat.getColor(mContext, R.color.green));
        }
    }
}
