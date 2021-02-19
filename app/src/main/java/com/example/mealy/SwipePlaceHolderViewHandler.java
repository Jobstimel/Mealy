package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SwipePlaceHolderViewHandler {

    public List<Integer> mStackIDs;
    public List<Object> mResolvers;

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public SwipePlaceHolderViewHandler(String mMode, SharedPreferences mSharedPreferences, Context mContext) {
        this.mMode = mMode;
        this.mSharedPreferences = mSharedPreferences;
        this.mContext = mContext;
    }

    public void loadSwipePlaceholderView(List<Integer> selectedIndices, List<Recipe> allRecipes, SwipePlaceHolderView swipePlaceHolderView) {
        swipePlaceHolderView.removeAllViews();
        if (!(selectedIndices.size() == 0)) {
            mStackIDs = new ArrayList<>();
            mResolvers = new ArrayList<>();
            for(int i = 0; i < selectedIndices.size(); i++){
                if (!PlayAlone.mLikedIDs.contains(selectedIndices.get(i)) && !PlayAlone.mDislikedIDs.contains(selectedIndices.get(i))) {
                    try {
                        swipePlaceHolderView.addView(new RecipeCard(mContext, allRecipes.get(selectedIndices.get(i)), swipePlaceHolderView));
                        mStackIDs.add(selectedIndices.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            mResolvers = swipePlaceHolderView.getAllResolvers();
        }
    }

    public void setSwipePlaceHolderViewBuilder(SwipePlaceHolderView swipePlaceHolderView) {
        swipePlaceHolderView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setSwipeDecor(new SwipeDecor()
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_yes)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_no)
                        .setSwipeRotationAngle(0)
                        .setSwipeDistToDisplayMsg(100)
                        .setSwipeAnimTime(200));
    }
}
