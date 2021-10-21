package com.kinopoisklite.security.google.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GoogleTokenService {
    @POST("/token")
    public Call<Map> getToken(@Query("code") String code,
                              @Query("client_id") String clientId,
                              @Query("client_secret") String clientSecret,
                              @Query("redirect_uri") String redirectUri,
                              @Query("grant_type") String grantType);
}
