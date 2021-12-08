package com.kinopoisklite.security.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.BuildConfig;
import com.kinopoisklite.security.TokenProvider;
import com.kinopoisklite.security.local.api.GoogleTokenService;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleTokenProvider implements TokenProvider {
    private GoogleTokenService tokenService;

    public GoogleTokenProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://oauth2.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tokenService = retrofit.create(GoogleTokenService.class);
    }

    public LiveData<Map> getToken(String authCode) throws IOException {
        MutableLiveData<Map> tokenData = new MutableLiveData<>();
        tokenService.getToken(authCode,
                BuildConfig.GOOGLE_CLIENT_ID,
                BuildConfig.GOOGLE_CLIENT_SECRET,
                BuildConfig.GOOGLE_REDIRECT_URI,
                "authorization_code").enqueue(new Callback<Map>() {
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
