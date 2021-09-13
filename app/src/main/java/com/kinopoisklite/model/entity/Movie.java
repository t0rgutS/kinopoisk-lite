package com.kinopoisklite.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Movie {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    protected Long id;
    @ColumnInfo
    protected String title;
    @ColumnInfo(name = "release_year")
    protected Integer releaseYear;
    @ColumnInfo
    protected Integer duration;
    @ColumnInfo
    protected String description;
    @Ignore
    private AgeRating ageRating;
    //private String coverUrl;
    //private String trailerUrl;

    public Movie(JSONObject object) throws JSONException {
        id = object.getLong("id");
        title = object.getString("title");
        releaseYear = object.getInt("releaseYear");
        duration = object.getInt("duration");
        description = object.getString("description");
        if(object.has("ageRating"))
            ageRating = new AgeRating(object.getJSONObject("ageRating"));
      //  coverUrl = object.getString("coverUrl");
      //  trailerUrl = object.getString("trailerUrl");
    }
}
