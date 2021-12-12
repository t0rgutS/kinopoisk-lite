package com.kinopoisklite.security.network.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerTokenService {
    @POST("google")
    Call<Map> getGoogleToken(@Query("code") String code);

    @POST("auth/touch")
    Call<ResponseBody> checkConnection(@Header("Authorization") String bearerToken);

    @POST("auth")
    Call<Map> getToken(@Header("Authorization") String credentials);

    @POST("auth/refresh")
    Call<Map> refreshToken(@Header("Authorization") String bearerToken,
                           @Body Map request);
}
