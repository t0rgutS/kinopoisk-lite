package com.kinopoisklite.repository.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.BuildConfig;
import com.kinopoisklite.repository.network.api.KinopoiskApi;
import com.kinopoisklite.repository.network.model.GenresAndCountries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExternalDataProvider {
    private KinopoiskApi api;

    public ExternalDataProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kinopoiskapiunofficial.tech/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(KinopoiskApi.class);
    }

    public LiveData<Map<String, List<String>>> getGenresAndCountries() {
        MutableLiveData<Map<String, List<String>>> genresAndCountries = new MutableLiveData<>();
        api.getGenresAndCountries(BuildConfig.KINOPOISK_API_KEY).enqueue(new Callback<GenresAndCountries>() {
            @Override
            public void onResponse(Call<GenresAndCountries> call, Response<GenresAndCountries> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, List<String>> map = new HashMap<>();
                    map.put("genres", response.body().getGenres()
                            .stream().map(genre -> genre.getGenre()).collect(Collectors.toList()));
                    map.put("countries", response.body().getCountries()
                            .stream().map(country -> country.getCountry()).collect(Collectors.toList()));
                    genresAndCountries.setValue(map);
                }
            }

            @Override
            public void onFailure(Call<GenresAndCountries> call, Throwable t) {

            }
        });
        return genresAndCountries;
    }
}
