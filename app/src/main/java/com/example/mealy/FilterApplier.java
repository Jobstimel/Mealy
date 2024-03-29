package com.example.mealy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FilterApplier {

    private final String[] COUNTRIES = {"Deutschland","Spanien","Asien","Italien","Frankreich","Italien","Griechenland","Indien"};
    private final String[] TYPES = {"Frühstück","Mittagessen","Abendessen","Dessert","Snack","Getränk"};
    private final String[] DIFFICULTIES = {"Einfach","Mittel","Schwierig"};
    public static final Integer MAX_RECIPES = 10;
    public final Integer MAX_CALORIES = 2000;
    public final Integer MAX_TIME = 300;

    private SharedPreferences mSharedPreferences;
    private List<Recipe> mAllRecipesList;
    private Context mContext;
    private String mMode;

    private List<Integer> mFilteredIDs;
    private List<Integer> mSelectedIDs;

    public FilterApplier(SharedPreferences sharedPreferences, List<Recipe> allRecipesList, Context context, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mAllRecipesList = allRecipesList;
        this.mContext = context;
        this.mMode = mode;
    }

    public void applyFilter(TextView textView, TextView button) {

        mFilteredIDs = new ArrayList<>();
        List<String> mTypeFilter = getFilterValues(TYPES);
        List<String> mCountryFilter = getFilterValues(COUNTRIES);
        List<String> mDifficultyFilter = getFilterValues(DIFFICULTIES);

        for (int i = 0; i < this.mAllRecipesList.size(); i++) {

            Recipe recipe = this.mAllRecipesList.get(i);
            String[] tags = recipe.getTags();
            Boolean status = true;

            if (mTypeFilter.size() > 0) {
                status = checkTagsMultipleSelections(tags, mTypeFilter);
            }
            if (status && mDifficultyFilter.size() > 0) {
                status = checkDifficulty(recipe, mDifficultyFilter);
            }
            if (status && mCountryFilter.size() > 0) {
                status = checkTagsMultipleSelections(tags, mCountryFilter);
            }
            if (status) {
                status = checkTime(recipe);
            }
            if (status) {
                status = checkCalories(recipe);
            }
            if (status) {
                status = checkTagsSingleValue(recipe.getAllergies(), "AllergiesSpinner"+mMode+"Value");
            }
            if (status) {
                status = checkTagsSingleValue(tags, "PreparationTypeSpinner"+mMode+"Value");
            }
            if (status) {
                status = checkTagsSingleValue(tags, "CategorySpinner"+mMode+"Value");
            }
            if (status) {
                status = checkTagsSingleValue(tags, "EatingTypeSpinner"+mMode+"Value");
            }

            if (status) {
                mFilteredIDs.add(recipe.getIndex());
            }
        }
        saveFilteredIDs();
        if (mSharedPreferences.getBoolean("ChangeStatus"+mMode, false) || mSharedPreferences.getString("Selected"+mMode+"IDs","").equals("")) {
            selectIndices(mFilteredIDs);
            deleteLikedIDs();
            deleteDislikedIDs();
        }
        int size = mFilteredIDs.size();
        if (size < 3) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            button.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            button.setText("Filter anpassen");
            if (mMode.equals("Online")) {
                makeToast("Damit eine Gruppe erstellt werden kann muss es mindestens 3 Filter Treffer geben");
            }
            else {
                makeToast("Es muss mindestens 3 Filter Treffer geben, damit du swipen kannst");
            }
            button.setClickable(false);
        }
        else {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            if (mMode.equals("Online")) {
                button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                button.setText("Gruppe erstellen");
            }
            else {
                button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                button.setText("Swipen");
            }
            button.setClickable(true);
        }
        textView.setText(size + " Rezepte gefunden");
    }

    private Boolean checkTagsMultipleSelections(String[] tags, List<String> filterValues) {
        Boolean found = false;
        for (int i = 0; i < tags.length; i++) {
            if (filterValues.contains(tags[i])) {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }
        return true;
    }

    private Boolean checkDifficulty(Recipe recipe, List<String> filterValues) {
        String diff = recipe.getDifficulty().toLowerCase();
        Boolean found = false;
        for (int i = 0; i < filterValues.size(); i++) {
            if (filterValues.get(i).equals(diff)) {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }
        return true;
    }

    private Boolean checkTime(Recipe recipe) {
        int timeSeekBarMin = mSharedPreferences.getInt("TimeSeekBar"+this.mMode+"Min",0);
        int timeSeekBarMax = mSharedPreferences.getInt("TimeSeekBar"+this.mMode+"Max", MAX_TIME);
        if (recipe.getTotal() >= timeSeekBarMin && recipe.getTotal() <= timeSeekBarMax) {
            return true;
        }
        return false;
    }

    private Boolean checkCalories(Recipe recipe) {
        int kcalSeekBarMin = mSharedPreferences.getInt("CaloriesSeekBar"+this.mMode+"Min",0);
        int kcalSeekBarMax = mSharedPreferences.getInt("CaloriesSeekBar"+this.mMode+"Max", MAX_CALORIES);
        if (recipe.getKcal() >= kcalSeekBarMin && recipe.getKcal() <= kcalSeekBarMax) {
            return true;
        }
        return false;
    }

    private Boolean checkTagsSingleValue(String[] tags, String name) {
        String tmp = mSharedPreferences.getString(name,"");
        if (!tmp.equals("")) {
            if (Arrays.asList(tags).contains(tmp)) {
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    private List<String> getFilterValues(String[] values) {
        List<String> tmp = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            if (mSharedPreferences.getBoolean(values[i]+mMode, false)) {
                tmp.add(values[i].toLowerCase());
            }
        }
        return tmp;
    }

    private void saveFilteredIDs() {
        String tmp = "";
        if (mFilteredIDs.size() > 0) {
            tmp = String.valueOf(mFilteredIDs.get(0));
            for (int i = 1; i < mFilteredIDs.size(); i++) {
                tmp = tmp + "#" + mFilteredIDs.get(i);
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Filtered"+this.mMode+"IDs", tmp);
        editor.commit();
    }

    private void selectIndices(List<Integer> filteredIDs) {
        mSelectedIDs = new ArrayList<>();
        if (this.MAX_RECIPES < filteredIDs.size()) {
            Random random = new Random();
            while (mSelectedIDs.size() < this.MAX_RECIPES) {
                int randomInt = random.nextInt(filteredIDs.size());
                if (!mSelectedIDs.contains(filteredIDs.get(randomInt))) {
                    mSelectedIDs.add(filteredIDs.get(randomInt));
                }
            }
        }
        else {
            for (int i = 0; i < filteredIDs.size(); i++) {
                mSelectedIDs.add(filteredIDs.get(i));
            }
        }
        saveSelectedIndices();
        setChangeStatusFalse();
    }

    private void saveSelectedIndices() {
        String tmp = "";
        if (mSelectedIDs.size() > 0) {
            tmp = String.valueOf(mSelectedIDs.get(0));
            for (int i = 1; i < mSelectedIDs.size(); i++) {
                tmp = tmp + "#" + mSelectedIDs.get(i);
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Selected"+mMode+"IDs", tmp);
        editor.commit();
    }

    private void setChangeStatusFalse() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ChangeStatus"+mMode, false);
        editor.commit();
    }

    private void deleteLikedIDs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Liked"+mMode+"IDs", "");
        editor.commit();
    }

    private void deleteDislikedIDs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Disliked"+mMode+"IDs", "");
        editor.commit();
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
