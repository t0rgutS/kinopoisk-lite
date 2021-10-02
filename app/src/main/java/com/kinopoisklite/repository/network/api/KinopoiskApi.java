package com.kinopoisklite.repository.network.api;

import com.kinopoisklite.repository.network.model.GenresAndCountries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface KinopoiskApi {
    @GET("api/v2.1/films/filters")
    Call<GenresAndCountries> getGenresAndCountries(@Header("X-API-KEY") String token);
}
