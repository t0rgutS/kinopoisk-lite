package com.kinopoisklite.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgeRating {
    private Long id;
    private String ratingCategory;
    private String minAge;

    public AgeRating(JSONObject object) throws JSONException {
        this.id = object.getLong("id");
        this.ratingCategory = object.getString("ratingCategory");
        this.minAge = object.getString("minAge");
    }

    public AgeRating(String ratingCategory) {
        this.ratingCategory = ratingCategory;
    }
}
