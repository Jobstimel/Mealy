package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

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

    public void applyCaloriesSeekBar(CrystalRangeSeekbar seekBarCalories, int min, int max, FilterApplier filterApplier, TextView textView) {
        if (!(mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Min", 0) == min) || !(mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Max", MAX_CALORIES) == max)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("CaloriesSeekBar"+mMode+"Min", min);
            editor.putInt("CaloriesSeekBar"+mMode+"Max", max);
            editor.putBoolean("ChangeStatus"+mMode, true);
            editor.commit();
        }
        filterApplier.applyFilter(textView);
    }

    public void applyTimeSeekBar(CrystalRangeSeekbar seekBarTime, int min, int max, FilterApplier filterApplier, TextView textView) {
        if (!(mSharedPreferences.getInt("TimeSeekBar"+mMode+"Min", 0) == min) || !(mSharedPreferences.getInt("TimeSeekBar"+mMode+"Max", MAX_TIME) == max)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("TimeSeekBar"+mMode+"Min", min);
            editor.putInt("TimeSeekBar"+mMode+"Max", max);
            editor.putBoolean("ChangeStatus"+mMode, true);
            editor.commit();
        }
        filterApplier.applyFilter(textView);
    }

    public void resetSeekBarStates(CrystalRangeSeekbar seekBarCalories, CrystalRangeSeekbar seekBarTime) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("CaloriesSeekBar"+mMode+"Min", 0);
        editor.putInt("CaloriesSeekBar"+mMode+"Max", this.MAX_CALORIES);
        editor.putInt("TimeSeekBar"+mMode+"Min", 0);
        editor.putInt("TimeSeekBar"+mMode+"Max", this.MAX_TIME);
        editor.commit();
        loadSeekBarStates(seekBarCalories, seekBarTime);
    }

    public void loadSeekBarStates(CrystalRangeSeekbar seekBarCalories, CrystalRangeSeekbar seekBarTime) {
        seekBarCalories.setMinStartValue(mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Min",0));
        seekBarCalories.setMaxStartValue(mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Max", this.MAX_CALORIES));
        seekBarCalories.apply();
        seekBarTime.setMinStartValue(mSharedPreferences.getInt("TimeSeekBar"+mMode+"Min",0));
        seekBarTime.setMaxStartValue(mSharedPreferences.getInt("TimeSeekBar"+mMode+"Max", this.MAX_TIME));
        seekBarTime.apply();
    }
}
