package com.kinopoisklite.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kinopoisklite.R;
import com.kinopoisklite.model.AgeRating;

import java.util.List;

public class AgeRatingAdapter extends ArrayAdapter<AgeRating> {
    private List<AgeRating> ageRatings;
    private int resource;
    private Context context;

    public AgeRatingAdapter(@NonNull Context context, int resource, @NonNull List<AgeRating> ageRatings) {
        super(context, resource, ageRatings);
        this.context = context;
        this.resource = resource;
        this.ageRatings = ageRatings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AgeRating rating = ageRatings.get(position);
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(resource, null);
        TextView textView = (TextView) convertView;
        textView.setTextColor(context.getResources().getColor(R.color.colorGreen));
        textView.setText(rating.getRatingCategory());
        return convertView;
    }

    @Override
    public int getPosition(@Nullable AgeRating item) {
        AgeRating found = ageRatings.stream().filter(el -> {
            if(item != null)
                return el.getRatingCategory().equals(item.getRatingCategory());
            else
                return false;
        }).findFirst().orElse(null);
        return ageRatings.indexOf(found);
    }

    @Override
    public int getCount() {
        if (ageRatings == null)
            return 0;
        return ageRatings.size();
    }
}
