package com.kinopoisklite.model.entity;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Movie {
    protected Long id;
    protected String title;
    protected Integer releaseYear;
    protected Integer duration;
    protected String description;
    private AgeRating ageRating;
    private String coverUrl;
    private String trailerUrl;

    public Movie(JSONObject object) throws JSONException {
        id = object.getLong("id");
        title = object.getString("title");
        releaseYear = object.getInt("releaseYear");
        duration = object.getInt("duration");
        description = object.getString("description");
        if(object.has("ageRating"))
            ageRating = new AgeRating(object.getJSONObject("ageRating"));
        coverUrl = object.getString("coverUrl");
        trailerUrl = object.getString("trailerUrl");
    }
}
