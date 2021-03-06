package com.kinopoisklite.repository.remote;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.repository.remote.model.MovieDTO;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.Repository;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

public class RemoteRepository implements Repository {
    private static final int THREAD_COUNT = 5;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    private volatile MutableLiveData<List<Movie>> movies;
    private volatile List<Movie> listMovies;
    private volatile MutableLiveData<List<AgeRating>> ageRatings;
    private volatile List<AgeRating> listRatings;

    public RemoteRepository() {
        movies = new MutableLiveData<>();
        ageRatings = new MutableLiveData<>();
        listMovies = new ArrayList<>();
        listRatings = new ArrayList<>();
    }

    @Override
    public LiveData<List<Movie>> getAllMovies() {
        executorService.execute(() -> {
            JSONArray jsonMovies = SingletonHttpOperator.INSTANCE
                    .getOperator().get("http://192.168.56.1/CourseMobile/movies.php");
            for (int i = 0; i < jsonMovies.length(); i++) {
                try {
                    MovieDTO movie = new MovieDTO(jsonMovies.getJSONObject(i));
                    if (!listMovies.contains(movie))
                        listMovies.add(movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    movies.setValue(listMovies);
                }
            });
        });
        return movies;
    }

    @SneakyThrows
    @Override
    public void deleteMovie(Movie movie) {
        executorService.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                if (SingletonHttpOperator.INSTANCE.getOperator()
                        .delete("http://192.168.56.1/CourseMobile/movies.php?id="
                                + movie.getId())) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listMovies.remove(movie);
                            movies.setValue(listMovies);
                        }
                    });
                }
            }
        });
    }

    @SneakyThrows
    @Override
    public void addMovie(Movie movie) {
        executorService.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                SingletonHttpOperator.INSTANCE.getOperator().post("http://192.168.56.1/CourseMobile/movies.php",
                        "arg=" + URLEncoder.encode(movie.toString()), "application/x-www-form-urlencoded");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listMovies.add(movie);
                        movies.setValue(listMovies);
                    }
                });
            }
        });
    }

    @Override
    public LiveData<List<AgeRating>> getAgeRatings() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonRatings = SingletonHttpOperator.INSTANCE
                        .getOperator().get("http://192.168.56.1/CourseMobile/supporting.php?table=age_ratings");
                for (int i = 0; i < jsonRatings.length(); i++) {
                    try {
                        AgeRating ageRating = new AgeRating(jsonRatings.getJSONObject(i));
                        if (!listRatings.contains(ageRating))
                            listRatings.add(ageRating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ageRatings.setValue(listRatings);
                    }
                });
            }
        });
        return ageRatings;
    }
}
