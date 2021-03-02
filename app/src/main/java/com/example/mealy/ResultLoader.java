package com.example.mealy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ResultLoader {

    private Context mContext;

    public ResultLoader(Context mContext) {
        this.mContext = mContext;
    }

    public void loadResults(List<Recipe> winners, List<ImageView> posters, List<TextView> scores) {
        for (int i = 0; i < winners.size(); i++) {
            loadResult(winners.get(i), posters.get(i), scores.get(i));
        }
    }

    private void loadResult(Recipe recipe, ImageView poster, TextView score) {
        Glide.with(mContext).load(recipe.getUrl1()).into(poster);
        score.setText(String.valueOf(recipe.getScore()));
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
