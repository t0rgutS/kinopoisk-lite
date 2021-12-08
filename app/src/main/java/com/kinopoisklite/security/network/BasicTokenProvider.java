package com.kinopoisklite.security.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.security.TokenProvider;
import com.kinopoisklite.security.network.api.ServerTokenService;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BasicTokenProvider implements TokenProvider {
    private ServerTokenService tokenService;

    public BasicTokenProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tokenService = retrofit.create(ServerTokenService.class);
    }

    @Override
    public LiveData<Map> getToken(String credentials) throws IOException {
        MutableLiveData<Map> tokenData = new MutableLiveData<>();
        tokenService.getToken("Basic "
                + Base64.getEncoder().encodeToString(credentials.getBytes())).enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
            }
        });
        return tokenData;
    }
}
