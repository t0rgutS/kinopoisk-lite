package com.kinopoisklite.repository.network.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GoogleApi {
    @GET("/oauth2/v1/userinfo")
    Call<Map> getUserInfo(@Header("Authorization") String bearerToken);
}
