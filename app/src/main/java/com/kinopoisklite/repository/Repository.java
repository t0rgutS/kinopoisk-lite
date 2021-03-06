package com.kinopoisklite.repository;

import androidx.lifecycle.LiveData;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;

import java.util.List;

public interface Repository {
    <T extends Movie> LiveData<List<T>> getAllMovies();

    LiveData<List<AgeRating>> getAgeRatings();

    <T extends Movie> void deleteMovie(T movie);

    <T extends Movie> void addMovie(T movie);
}
