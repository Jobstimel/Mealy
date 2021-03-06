package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.List;

public class FilterSpinnerHandler {

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private String mMode;

    private final String[] SPINNER_KEYS = {"AllergiesSpinner", "PreparationTypeSpinner", "CategorySpinner", "EatingTypeSpinner"};

    public FilterSpinnerHandler(SharedPreferences sharedPreferences, Context context, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mContext = context;
        this.mMode = mode;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void saveSpinnerState(String key, String newItem, int newIndex, LinearLayout layout) {
        if (!mSharedPreferences.getString(key + mMode + "Value", "").equals(newItem.toLowerCase())) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key + mMode + "Index", newIndex);
            editor.putString(key+ mMode + "Value", newItem.toLowerCase());
            editor.putBoolean("ChangeStatus" + mMode, true);
            editor.commit();
        }
        setLayoutSelected(layout);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerState(PowerSpinnerView powerSpinnerView, String key, FilterApplier filterApplier, TextView textView, LinearLayout layout, TextView button) {
        powerSpinnerView.clearSelectedItem();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key + mMode + "Index", -1);
        editor.putString(key + mMode + "Value", "");
        editor.putBoolean("ChangeStatus" + mMode, true);
        editor.commit();
        setLayoutUnselected(layout);
        filterApplier.applyFilter(textView, button);
    }

    public void resetSpinnerSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < SPINNER_KEYS.length; i++) {
            editor.putInt(SPINNER_KEYS[i]+mMode+"Index",-1);
            editor.putString(SPINNER_KEYS[i]+mMode+"Value", "");
        }
        editor.commit();
    }

    public void loadSpinnerStates(PowerSpinnerView view1, PowerSpinnerView view2, PowerSpinnerView view3, PowerSpinnerView view4) {
        view1.selectItemByIndex(mSharedPreferences.getInt("AllergiesSpinner"+mMode+"Index",-1));
        view2.selectItemByIndex(mSharedPreferences.getInt("PreparationTypeSpinner"+mMode+"Index",-1));
        view3.selectItemByIndex(mSharedPreferences.getInt("CategorySpinner"+mMode+"Index",-1));
        view4.selectItemByIndex(mSharedPreferences.getInt("EatingTypeSpinner"+mMode+"Index",-1));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerStates(List<PowerSpinnerView> list, FilterApplier filterApplier, TextView textView, List<LinearLayout> layouts, TextView button) {
        for (int i = 0; i < list.size(); i++) {
            resetSpinnerState(list.get(i), this.SPINNER_KEYS[i], filterApplier, textView, layouts.get(i), button);
        }
        loadSpinnerStates(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setLayoutSelected(LinearLayout layout) {
        layout.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.blue));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setLayoutUnselected(LinearLayout layout) {
        layout.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
    }
}
