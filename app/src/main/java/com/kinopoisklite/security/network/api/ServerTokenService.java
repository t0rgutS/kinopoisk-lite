package com.kinopoisklite.security.network.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerTokenService {
    @POST("/google")
    Call<Map> getGoogleToken(@Query("code") String code);

    @POST("/auth")
    Call<Map> getToken(@Header("Authorization") String bearerToken);
}
