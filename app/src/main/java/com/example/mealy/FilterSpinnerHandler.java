package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.List;

public class FilterSpinnerHandler {

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    private final String[] SPINNER_KEYS = {"AllergiesSpinner", "PreparationTypeSpinner", "CategorySpinner", "EatingTypeSpinner"};

    public FilterSpinnerHandler(String mode, SharedPreferences sharedPreferences, Context context) {
        this.mMode = mode;
        this.mSharedPreferences = sharedPreferences;
        this.mContext = context;
    }

    public void resetSpinner(PowerSpinnerView powerSpinnerView, String key, FilterApplier filterApplier, TextView textView) {
        powerSpinnerView.clearSelectedItem();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key+mMode+"Index", -1);
        editor.putString(key+mMode+"Value", "");
        editor.putBoolean("ChangeStatus"+mMode, true);
        editor.commit();
        powerSpinnerView.setTextColor(ContextCompat.getColor(mContext, R.color.filter_text_color));
        filterApplier.applyFilter(textView);
    }

    public void applySpinner(PowerSpinnerView powerSpinnerView, String key, String newItem, int newIndex) {
        if (!mSharedPreferences.getString(key+mMode+"Value", "").equals(newItem.toLowerCase())) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key+mMode+"Index", newIndex);
            editor.putString(key+mMode+"Value", newItem.toLowerCase());
            editor.putBoolean("ChangeStatus"+mMode, true);
            editor.commit();
        }
        powerSpinnerView.setTextColor(ContextCompat.getColor(mContext, R.color.green_transparent));
    }

    public void loadSpinnerStates(PowerSpinnerView view1, PowerSpinnerView view2, PowerSpinnerView view3, PowerSpinnerView view4) {
        view1.selectItemByIndex(mSharedPreferences.getInt("AllergiesSpinner"+mMode+"Index",-1));
        view2.selectItemByIndex(mSharedPreferences.getInt("PreparationTypeSpinner"+mMode+"Index",-1));
        view3.selectItemByIndex(mSharedPreferences.getInt("CategorySpinner"+mMode+"Index",-1));
        view4.selectItemByIndex(mSharedPreferences.getInt("EatingTypeSpinner"+mMode+"Index",-1));
    }

    public void resetSpinnerStates(List<PowerSpinnerView> list, FilterApplier filterApplier, TextView textView) {
        for (int i = 0; i < list.size(); i++) {
            resetSpinner(list.get(i), this.SPINNER_KEYS[i], filterApplier, textView);
        }
        loadSpinnerStates(list.get(0), list.get(1), list.get(2), list.get(3));
    }
}
