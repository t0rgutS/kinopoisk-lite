package com.kinopoisklite.security.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.security.TokenProvider;
import com.kinopoisklite.security.network.api.ServerTokenService;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleTokenProvider implements TokenProvider {
    private ServerTokenService tokenService;

    public GoogleTokenProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6:8080/api/oauth/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tokenService = retrofit.create(ServerTokenService.class);
    }

    @Override
    public LiveData<Map> getToken(String authCode) throws IOException {
        MutableLiveData<Map> tokenData = new MutableLiveData<>();
        tokenService.getGoogleToken(authCode).enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenData.setValue(response.body());
                } else if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка получения токена: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка получения токена: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {
            }
        });
        return tokenData;
    }
}
