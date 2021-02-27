package com.example.mealy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ResultLoader {

    private Context mContext;

    public ResultLoader(Context mContext) {
        this.mContext = mContext;
    }

    public void loadResult(Recipe recipe, TextView title, TextView score, ImageView poster) {
        String recipe_title = recipe.getTitle();
        if (recipe_title.length() > 45) {
            recipe_title = recipe_title.substring(0, 41).trim();
            recipe_title = recipe_title + "...";
        }
        title.setText(recipe_title);
        score.setText(String.valueOf(recipe.getScore()));
        Glide.with(mContext).load(recipe.getUrl1()).into(poster);

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipe.getUrl2()));
                openLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(openLinkIntent);
            }
        });
    }
}
