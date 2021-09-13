package com.kinopoisklite.model.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "age_ratings")
public class AgeRating {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    @ColumnInfo(name = "rating_category")
    @NotNull
    private String ratingCategory;
    @ColumnInfo(name = "min_age")
    @NotNull
    private Integer minAge;

    @Ignore
    public AgeRating(JSONObject object) throws JSONException {
        this.id = object.getLong("id");
        this.ratingCategory = object.getString("ratingCategory");
        this.minAge = object.getInt("minAge");
    }

    @Ignore
    public AgeRating(String ratingCategory) {
        this.ratingCategory = ratingCategory;
    }

    @Ignore
    public AgeRating(String ratingCategory, Integer minAge) {
        this.ratingCategory = ratingCategory;
        this.minAge = minAge;
    }

    @Ignore
    public AgeRating(Long id, String ratingCategory, Integer minAge) {
        this.id = id;
        this.ratingCategory = ratingCategory;
        this.minAge = minAge;
    }

    @NonNull
    @Override
    public String toString() {
        return ratingCategory;
    }
}
