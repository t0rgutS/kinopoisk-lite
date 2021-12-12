package com.kinopoisklite.repository.network.api;

import com.kinopoisklite.repository.network.model.ServerMovieDTO;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MovieApi {
    @GET("movies")
    Call<List<ServerMovieDTO>> getAllMovies();

    @GET("favorite")
    Call<List<ServerMovieDTO>> getFavoriteMovies(@Header("Authorization") String bearerToken);

    @GET("movies/{id}")
    Call<ServerMovieDTO> getMovie(@Path("id") String movieId);

    @GET("favorite/{id}")
    Call<Map> isFav(@Path("id") String movieId,
                    @Header("Authorization") String bearerToken);

    @POST("favorite/{id}")
    Call<ServerMovieDTO> addToFav(@Path("id") String movieId,
                                @Header("Authorization") String bearerToken);

    @DELETE("favorite/{id}")
    Call<ResponseBody> removeFromFav(@Path("id") String movieId,
                          @Header("Authorization") String bearerToken);

    @POST("movies")
    Call<ServerMovieDTO> createMovie(@Header("Authorization") String bearerToken,
                                     @Body ServerMovieDTO movie);

    @PUT("movies/{id}")
    Call<ServerMovieDTO> updateMovie(@Path("id") String movieId,
                                     @Header("Authorization") String bearerToken,
                                     @Body ServerMovieDTO movie);

    @DELETE("movies/{id}")
    Call<ResponseBody> deleteMovie(@Path("id") String movieId,
                                   @Header("Authorization") String bearerToken);
}
