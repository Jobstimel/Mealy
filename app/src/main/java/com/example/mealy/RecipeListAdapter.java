package com.example.mealy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    static class ViewHolder {
        TextView place;
        TextView proVotes;
        TextView againstVotes;
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

        String place = getItem(position).getPlace();
        String proVotes = String.valueOf(getItem(position).getScore());
        String againstVotes = String.valueOf(getItem(position).getAgainst());
        String title = getItem(position).getTitle();
        final String url2 = getItem(position).getUrl2();

        final View result;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            result = convertView;
            holder = new ViewHolder();
            holder.place = convertView.findViewById(R.id.recipe_place);
            holder.proVotes = convertView.findViewById(R.id.recipe_votes_for);
            holder.againstVotes = convertView.findViewById(R.id.recipe_votes_against);
            holder.title = convertView.findViewById(R.id.recipe_title);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position > lastPosition) ? R.anim.animation_load_down : R.anim.animation_load_up);
        result.startAnimation(animation);
        lastPosition = position;

        holder.place.setText(place);
        holder.proVotes.setText(proVotes);
        holder.againstVotes.setText(againstVotes);
        holder.title.setText(title);

        if (position%2 != 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.result_list_background));
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
