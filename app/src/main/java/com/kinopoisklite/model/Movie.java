package com.kinopoisklite.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

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
    protected String country;
    @ColumnInfo
    protected String genre;
    @ColumnInfo
    protected String description;
    @Ignore
    private AgeRating ageRating;
    @ColumnInfo(name = "cover_uri")
    private String coverUri;

    @Ignore
    public Movie(JSONObject object) throws JSONException {
        id = object.getLong("id");
        title = object.getString("title");
        releaseYear = object.getInt("releaseYear");
        duration = object.getInt("duration");
        description = object.getString("description");
        if (object.has("ageRating"))
            ageRating = new AgeRating(object.getJSONObject("ageRating"));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;
        Movie second = (Movie) obj;
        if (!this.id.equals(second.id)
                || !this.title.equals(second.title)
                || !this.releaseYear.equals(second.releaseYear)
                || !this.duration.equals(second.duration)
                || !this.ageRating.getRatingCategory().equals(second.ageRating.getRatingCategory())
                || !this.genre.equals(second.genre)
                || !this.country.equals(second.country)
        )
            return false;
        else if (this.description != second.description)
            if (!this.description.equals(second.description))
                return false;
            else if (this.coverUri != second.coverUri)
                return this.coverUri.equals(second.coverUri);
        return true;
    }
}
