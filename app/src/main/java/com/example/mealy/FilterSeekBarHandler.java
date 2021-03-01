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

    public void applyCaloriesSeekBar(CrystalRangeSeekbar seekBarCalories, int min, int max, FilterApplier filterApplier, TextView textView, TextView button) {
        if (!(mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Min", 0) == min) || !(mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Max", MAX_CALORIES) == max)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("CaloriesSeekBar"+mMode+"Min", min);
            editor.putInt("CaloriesSeekBar"+mMode+"Max", max);
            editor.putBoolean("ChangeStatus"+mMode, true);
            editor.commit();
        }
        setSeekBarUnselected(seekBarCalories);
        if (min > 0 || max < MAX_CALORIES) {
            setSeekBarSelected(seekBarCalories);
        }
        filterApplier.applyFilter(textView, button);
    }

    public void applyTimeSeekBar(CrystalRangeSeekbar seekBarTime, int min, int max, FilterApplier filterApplier, TextView textView, TextView button) {
        if (!(mSharedPreferences.getInt("TimeSeekBar"+mMode+"Min", 0) == min) || !(mSharedPreferences.getInt("TimeSeekBar"+mMode+"Max", MAX_TIME) == max)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt("TimeSeekBar"+mMode+"Min", min);
            editor.putInt("TimeSeekBar"+mMode+"Max", max);
            editor.putBoolean("ChangeStatus"+mMode, true);
            editor.commit();
        }
        setSeekBarUnselected(seekBarTime);
        if (min > 0 || max < MAX_TIME) {
            setSeekBarSelected(seekBarTime);
        }
        filterApplier.applyFilter(textView, button);
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

    public void resetSeekBarSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("CaloriesSeekBar"+mMode+"Min", 0);
        editor.putInt("CaloriesSeekBar"+mMode+"Max", this.MAX_CALORIES);
        editor.putInt("TimeSeekBar"+mMode+"Min", 0);
        editor.putInt("TimeSeekBar"+mMode+"Max", this.MAX_TIME);
        editor.commit();
    }

    public void loadSeekBarStates(CrystalRangeSeekbar seekBarCalories, CrystalRangeSeekbar seekBarTime) {
        int min_calories = mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Min",0);
        int max_calories = mSharedPreferences.getInt("CaloriesSeekBar"+mMode+"Max", this.MAX_CALORIES);
        seekBarCalories.setMinStartValue(min_calories);
        seekBarCalories.setMaxStartValue(max_calories);
        seekBarCalories.apply();

        int min_time = mSharedPreferences.getInt("TimeSeekBar"+mMode+"Min",0);
        int max_time = mSharedPreferences.getInt("TimeSeekBar"+mMode+"Max", this.MAX_TIME);
        seekBarTime.setMinStartValue(min_time);
        seekBarTime.setMaxStartValue(max_time);
        seekBarTime.apply();

        setSeekBarUnselected(seekBarCalories);
        if (min_calories > 0 || max_calories < MAX_CALORIES) {
            setSeekBarSelected(seekBarCalories);
        }

        setSeekBarUnselected(seekBarTime);
        if (min_time > 0 || max_time < MAX_TIME) {
            setSeekBarSelected(seekBarTime);
        }
    }

    private void setSeekBarSelected(CrystalRangeSeekbar seekBar) {
        seekBar.setBarHighlightColor(ContextCompat.getColor(mContext, R.color.filter_green));
        seekBar.setLeftThumbColor(ContextCompat.getColor(mContext, R.color.filter_green));
        seekBar.setRightThumbColor(ContextCompat.getColor(mContext, R.color.filter_green));
    }

    private void setSeekBarUnselected(CrystalRangeSeekbar seekBar) {
        seekBar.setBarHighlightColor(ContextCompat.getColor(mContext, R.color.filter_border_color));
        seekBar.setLeftThumbColor(ContextCompat.getColor(mContext, R.color.filter_border_color));
        seekBar.setRightThumbColor(ContextCompat.getColor(mContext, R.color.filter_border_color));
    }

}
