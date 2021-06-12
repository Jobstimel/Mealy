package com.example.mealy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

    private int lastPosition = -1;
    private Context mContext;
    private int mResource;

    static class ViewHolder {
        TextView proVotes;
        ImageView url1;
        TextView title;
    }

    public RecipeListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Recipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String url1 = getItem(position).getUrl1();
        Integer proVotes = getItem(position).getScore();
        String title = getItem(position).getTitle();
        final String url2 = getItem(position).getUrl2();

        final View result;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            result = convertView;
            holder = new ViewHolder();
            holder.proVotes = convertView.findViewById(R.id.recipe_votes_for);
            holder.title = convertView.findViewById(R.id.recipe_title);
            holder.url1 = convertView.findViewById(R.id.recipe_image);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position > lastPosition) ? R.anim.animation_load_down : R.anim.animation_load_up);
        result.startAnimation(animation);
        lastPosition = position;

        Glide.with(mContext).load(url1).into(holder.url1);
        holder.proVotes.setText(String.valueOf(proVotes));
        holder.title.setText(title);

        holder.proVotes.setTextColor(Color.parseColor("#32BA7C"));

        if (proVotes <= 0) {
            holder.proVotes.setTextColor(Color.parseColor("#FF0006"));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                openLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(openLinkIntent);
            }
        });

        return convertView;
    }
}
