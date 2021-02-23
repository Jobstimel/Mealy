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
    public void saveFilterValue(View v, FilterApplier filterApplier, TextView recipeCount, List<TextView> textViewList)  {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        TextView textView = textViewList.get(TOOLTIPS.indexOf(v.getTooltipText()));
        if (!mSharedPreferences.getBoolean(v.getTooltipText() +mMode,false)) {
            editor.putBoolean(v.getTooltipText() +mMode, true);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.green_transparent));
        }
        else {
            editor.putBoolean(v.getTooltipText() +mMode, false);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.filter_text_color));
        }
        editor.putBoolean("ChangeStatusOffline", true);
        editor.commit();
        filterApplier.applyFilter(recipeCount);
    }

    public void loadFilterLayoutStates(List<TextView> list) {
        for (int i = 0; i < list.size(); i++) {
            if (mSharedPreferences.getBoolean(this.TOOLTIPS.get(i)+mMode, false)) {
                list.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.green_transparent));
            }
        }
    }

    public void resetLayouts(List<TextView> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < list.size(); i++) {
            editor.putBoolean(this.TOOLTIPS.get(i)+mMode, false);
            list.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.filter_text_color));
        }
        editor.putBoolean("ChangeStatusOffline", true);
        editor.commit();
        loadFilterLayoutStates(list);
    }
}
