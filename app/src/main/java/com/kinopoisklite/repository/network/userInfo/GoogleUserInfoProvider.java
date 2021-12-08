package com.kinopoisklite.repository.network.userInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.repository.network.api.GoogleApi;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleUserInfoProvider implements UserInfoProvider {
    private GoogleApi api;

    public GoogleUserInfoProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(GoogleApi.class);
    }

    public LiveData<Map> getUserInfo(String token) throws IOException {
        MutableLiveData<Map> userInfo = new MutableLiveData<>();
        api.getUserInfo("Bearer " + token).enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                if (response.isSuccessful() && response.body() != null)
                    userInfo.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Map> call, Throwable t) {

            }
        });
        return userInfo;
    }
}