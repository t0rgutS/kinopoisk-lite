package com.kinopoisklite.repository.network.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApi {
    @GET("auth/me")
    Call<Map> getUserInfo(@Header("Authorization") String bearerToken);

    @PUT("auth/me")
    Call<ResponseBody> updateUserInfo(@Header("Authorization") String bearerToken,
                                      @Body Map<String, String> updateRequest);
}