package com.example.mealy;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
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
public class RecipeCard {

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

    private Recipe mRecipe;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public RecipeCard(Context context, Recipe recipe, SwipePlaceHolderView swipeView) throws JSONException {
        mContext = context;
        mRecipe = recipe;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mRecipe.getUrl1()).into(recipeImage);
        recipeTitle.setText(mRecipe.getTitle());
        recipeTotal.setText(mRecipe.getTotal()+" min");
        recipePrepare.setText(mRecipe.getTotal()+" min");
        recipeDifficulty.setText(mRecipe.getDifficulty());
        recipeIngredients.setText(mRecipe.getIngredients());
    }

    @SwipeOut
    private void onSwipedOut() {
        List<Object> tmp = mSwipeView.getAllResolvers();
        for (int i = 0; i < PlayAlone.mResolvers.size(); i++) {
            if (PlayAlone.mResolvers.get(i) == tmp.get(0)) {
                PlayAlone.mDislikedIDs.add(PlayAlone.mStackIDs.get(i));
            }
        }
    }

    @SwipeCancelState
    private void onSwipeCancelState() { }

    @SwipeIn
    private void onSwipeIn() {
        List<Object> tmp = mSwipeView.getAllResolvers();
        for (int i = 0; i < PlayAlone.mResolvers.size(); i++) {
            if (PlayAlone.mResolvers.get(i) == tmp.get(0)) {
                PlayAlone.mLikedIDs.add(PlayAlone.mStackIDs.get(i));
            }
        }
    }

    @SwipeInState
    private void onSwipeInState(){ }

    @SwipeOutState
    private void onSwipeOutState(){
    }
}
