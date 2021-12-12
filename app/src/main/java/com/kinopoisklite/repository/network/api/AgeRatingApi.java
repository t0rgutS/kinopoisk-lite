package com.kinopoisklite.repository.network.api;

import com.kinopoisklite.model.AgeRating;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AgeRatingApi {
    @GET("ageRatings")
    Call<List<AgeRating>> getAllRatings();

    @GET("ageRatings/{id}")
    Call<AgeRating> getRating(@Path("id") String ageRatingId);
}
