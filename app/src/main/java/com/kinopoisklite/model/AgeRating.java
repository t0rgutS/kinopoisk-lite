package com.kinopoisklite.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "age_ratings")
public class AgeRating {
    @PrimaryKey
    @ColumnInfo
    @NonNull
    private String id;
    @ColumnInfo(name = "rating_category")
    @NonNull
    private String ratingCategory;
    @ColumnInfo(name = "min_age")
    @NotNull
    private Integer minAge;

    @Ignore
    public AgeRating(JSONObject object) throws JSONException {
        this.id = object.getString("id");
        this.ratingCategory = object.getString("ratingCategory");
        this.minAge = object.getInt("minAge");
    }

    @Ignore
    public AgeRating(String ratingCategory) {
        this.id = UUID.randomUUID().toString();
        this.ratingCategory = ratingCategory;
    }

    @Ignore
    public AgeRating(String ratingCategory, Integer minAge) {
        this.id = UUID.randomUUID().toString();
        this.ratingCategory = ratingCategory;
        this.minAge = minAge;
    }

    @Ignore
    public AgeRating(String id, String ratingCategory, Integer minAge) {
        this.id = id;
        this.ratingCategory = ratingCategory;
        this.minAge = minAge;
    }

    public AgeRating() {
        this.id = UUID.randomUUID().toString();
    }

    @NonNull
    @Override
    public String toString() {
        return ratingCategory;
    }
}
