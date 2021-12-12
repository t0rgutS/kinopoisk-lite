package com.kinopoisklite.repository.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.BuildConfig;
import com.kinopoisklite.repository.network.api.KinopoiskApi;
import com.kinopoisklite.repository.network.model.GenresAndCountries;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundInfoProvider {
    private KinopoiskApi api;
    private MutableLiveData<List<String>> genres;
    private MutableLiveData<List<String>> countries;

    public BackgroundInfoProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kinopoiskapiunofficial.tech/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(KinopoiskApi.class);
        genres = new MutableLiveData<>();
        countries = new MutableLiveData<>();
        getGenresAndCountries();
    }

    public LiveData<List<String>> getCountries() {
        return countries;
    }

    public LiveData<List<String>> getGenres() {
        return genres;
    }

    private void getGenresAndCountries() {
        api.getGenresAndCountries(BuildConfig.KINOPOISK_API_KEY).enqueue(new Callback<GenresAndCountries>() {
            @Override
            public void onResponse(Call<GenresAndCountries> call, Response<GenresAndCountries> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genres.setValue(response.body().getGenres()
                            .stream().map(genre -> genre.getGenre()).collect(Collectors.toList()));
                    countries.setValue(response.body().getCountries()
                            .stream().map(country -> country.getCountry()).collect(Collectors.toList()));
                }
            }

            @Override
            public void onFailure(Call<GenresAndCountries> call, Throwable t) {

            }
        });
    }
}
