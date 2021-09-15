package com.kinopoisklite.repository.remote.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.model.Movie;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RemoteMovieDTO extends Movie {
    private String ratingCategory;

    public RemoteMovieDTO(JSONObject object) throws JSONException {
        id = object.getLong("id");
        title = object.getString("title");
        releaseYear = object.getInt("releaseYear");
        duration = object.getInt("duration");
        description = object.getString("description");
        setRatingCategory(object.getString("ratingCategory"));
    }

    public void setRatingCategory(String ratingCategory) {
        this.ratingCategory = ratingCategory;
        super.setAgeRating(new AgeRating(ratingCategory));
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
