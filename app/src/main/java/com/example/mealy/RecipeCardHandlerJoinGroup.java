package com.example.mealy;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.json.JSONException;

import java.util.List;

@Layout(R.layout.recipe_card_view)
public class RecipeCardHandlerJoinGroup {

    @View(R.id.recipe_image)
    private ImageView recipeImage;

    @View(R.id.recipe_title)
    private TextView recipeTitle;

    @View(R.id.recipe_total)
    private TextView recipeTotal;

    @View(R.id.recipe_prepare)
    private TextView recipePrepare;

    @View(R.id.recipe_difficulty)
    private TextView recipeDifficulty;

    @View(R.id.recipe_ingredients)
    private TextView recipeIngredients;

    @View(R.id.text_view_calories)
    private TextView recipeCalories;

    @View(R.id.text_view_fat)
    private TextView recipeFat;

    @View(R.id.text_view_carbs)
    private TextView recipeCarbs;

    @View(R.id.text_view_protein)
    private TextView recipeProtein;

    @View(R.id.text_view_fiber)
    private TextView recipeFiber;

    private Recipe mRecipe;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private TextView mPlaceholder;

    public RecipeCardHandlerJoinGroup(Context context, Recipe recipe, SwipePlaceHolderView swipeView) throws JSONException {
        this.mContext = context;
        this.mRecipe = recipe;
        this.mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mRecipe.getUrl1()).into(recipeImage);
        recipeTitle.setText(mRecipe.getTitle());
        recipeTotal.setText(mRecipe.getTotal()+" min");
        recipePrepare.setText(mRecipe.getTotal()+" min");
        recipeDifficulty.setText(mRecipe.getDifficulty());
        recipeIngredients.setText(mRecipe.getIngredients());
        recipeCalories.setText(mRecipe.getCalories());
        recipeFat.setText(mRecipe.getFat());
        recipeCarbs.setText(mRecipe.getCarbs());
        recipeProtein.setText(mRecipe.getProtein());
        recipeFiber.setText(mRecipe.getFiber());
    }

    @SwipeOut
    private void onSwipedOut() {
        List<Object> tmp = mSwipeView.getAllResolvers();
        for (int i = 0; i < ActivityJoinGroup.mResolvers.size(); i++) {
            if (ActivityJoinGroup.mResolvers.get(i) == tmp.get(0)) {
                ActivityJoinGroup.mDislikedIDs.add(ActivityJoinGroup.mStackIDs.get(i));
            }
        }
    }

    @SwipeCancelState
    private void onSwipeCancelState() {

    }

    @SwipeIn
    private void onSwipeIn() {
        List<Object> tmp = mSwipeView.getAllResolvers();
        for (int i = 0; i < ActivityJoinGroup.mResolvers.size(); i++) {
            if (ActivityJoinGroup.mResolvers.get(i) == tmp.get(0)) {
                ActivityJoinGroup.mLikedIDs.add(ActivityJoinGroup.mStackIDs.get(i));
            }
        }
    }

    @SwipeInState
    private void onSwipeInState() {

    }

    @SwipeOutState
    private void onSwipeOutState() {

    }
}
