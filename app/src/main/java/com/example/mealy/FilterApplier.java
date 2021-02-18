package com.example.mealy;

import android.content.SharedPreferences;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterApplier {

    public final Integer MAX_TIME = 300;
    public final Integer MAX_CALORIES = 2000;
    private final String[] TYPES = {"Frühstück","Mittagessen","Abendessen","Dessert","Snack","Getränk"};
    private final String[] COUNTRIES = {"Deutschland","Spanien","Asien","Italien","Frankreich","Italien","Griechenland","Indien"};
    private final String[] DIFFICULTIES = {"Einfach","Mittel","Schwierig"};

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private List<Recipe> mAllRecipesList;
    private List<Recipe> mFilteredRecipeList;
    private List<Integer> mFilteredIDs;

    public FilterApplier(String mode, SharedPreferences sharedPreferences, List<Recipe> allRecipesList) {
        this.mMode = mode;
        this.mSharedPreferences = sharedPreferences;
        this.mAllRecipesList = allRecipesList;
    }

    public void applyFilter(TextView textView) {

        mFilteredRecipeList = new ArrayList<>();
        List<String> mTypeFilter = getFilterValues(TYPES);
        List<String> mCounFilter = getFilterValues(COUNTRIES);
        List<String> mDiffFilter = getFilterValues(DIFFICULTIES);

        for (int i = 0; i < this.mAllRecipesList.size(); i++) {

            Recipe recipe = this.mAllRecipesList.get(i);
            String[] tags = recipe.getTags();
            Boolean status = true;

            if (status && mTypeFilter.size() > 0) {
                status = checkTagsMultipleSelections(tags, mTypeFilter);
            }
            if (mDiffFilter.size() > 0) {
                status = checkDifficulty(recipe, mDiffFilter);
            }
            if (status && mCounFilter.size() > 0) {
                status = checkTagsMultipleSelections(tags, mCounFilter);
            }
            if (status) {
                status = checkTime(recipe);
            }
            if (status) {
                status = checkCalories(recipe);
            }
            if (status) {
                status = checkTagsSingleValue(recipe.getAllergies(), "AllergiesSpinner"+this.mMode+"Value");
            }
            if (status) {
                status = checkTagsSingleValue(tags, "PreparationTypeSpinner"+this.mMode+"Value");
            }
            if (status) {
                status = checkTagsSingleValue(tags, "CategorySpinner"+this.mMode+"Value");
            }
            if (status) {
                status = checkTagsSingleValue(tags, "EatingTypeSpinner"+this.mMode+"Value");
            }

            if (status) {
                mFilteredRecipeList.add(recipe);
            }
        }
        saveFilteredIDs();
        textView.setText(mFilteredRecipeList.size() + " Rezepte gefunden");
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
        mFilteredIDs = new ArrayList<Integer>();
        if (mFilteredRecipeList.size() > 0) {
            tmp = String.valueOf(mFilteredRecipeList.get(0).getIndex());
            mFilteredIDs.add(mFilteredRecipeList.get(0).getIndex());
            for (int i = 1; i < mFilteredRecipeList.size(); i++) {
                tmp = tmp+"#"+ mFilteredRecipeList.get(i).getIndex();
                mFilteredIDs.add(mFilteredRecipeList.get(i).getIndex());
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Filtered"+this.mMode+"IDs", tmp);
        editor.commit();
    }
}
