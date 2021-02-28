package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class FilterLinearLayoutHandler {

    public final List<String> TOOLTIPS = Arrays.asList("Einfach","Mittel","Schwierig","Frühstück","Mittagessen","Abendessen","Dessert","Snack","Getränk","Deutschland","Spanien","Asien","Italien","Frankreich","Griechenland","Indien");

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public FilterLinearLayoutHandler(String mMode, SharedPreferences mSharedPreferences, Context mContext) {
        this.mMode = mMode;
        this.mSharedPreferences = mSharedPreferences;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v, FilterApplier filterApplier, TextView recipeCount, TextView button)  {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(v.getTooltipText() +mMode,false)) {
            editor.putBoolean(v.getTooltipText() +mMode, true);
            setViewSelected(v);
        }
        else {
            editor.putBoolean(v.getTooltipText() +mMode, false);
            setViewUnselected(v);
        }
        editor.putBoolean("ChangeStatus"+mMode, true);
        editor.commit();
        filterApplier.applyFilter(recipeCount, button);
    }

    public void loadFilterLayoutStates(List<LinearLayout> list) {
        for (int i = 0; i < list.size(); i++) {
            if (mSharedPreferences.getBoolean(this.TOOLTIPS.get(i)+mMode, false)) {
                setLayoutSelected(list.get(i));
            }
        }
    }

    public void resetLayouts(List<LinearLayout> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < list.size(); i++) {
            editor.putBoolean(this.TOOLTIPS.get(i)+mMode, false);
            setLayoutUnselected(list.get(i));
        }
        editor.putBoolean("ChangeStatus"+mMode, true);
        editor.commit();
        loadFilterLayoutStates(list);
    }

    public void resetLayoutSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < TOOLTIPS.size(); i++) {
            editor.putBoolean(TOOLTIPS.get(i)+mMode, false);
        }
        editor.commit();
    }

    private void setLayoutSelected(LinearLayout layout) {
        layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.filter_green));
    }

    private void setLayoutUnselected(LinearLayout layout) {
        layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.filter_background_color));
    }

    private void setViewSelected(View view) {
        view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.filter_green));
    }

    private void setViewUnselected(View view) {
        view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.filter_background_color));
    }
}
