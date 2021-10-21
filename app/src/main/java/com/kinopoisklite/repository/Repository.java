package com.kinopoisklite.repository;

import androidx.lifecycle.LiveData;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.Role;
import com.kinopoisklite.model.User;

import java.util.List;

public interface Repository {
    <T extends Movie> LiveData<List<T>> getAllMovies();

    LiveData<List<Movie>> getUserFavourites(String id);

    LiveData<List<AgeRating>> getAgeRatings();

    <T extends Movie> void deleteMovie(T movie);

    <T extends Movie> void updateMovie(T movie);

    <T extends Movie> void addMovie(T movie);

    <T extends User> T getUserById(String id);

    <T extends User> LiveData<T> getUserByLogin(String login);

    <T extends User> void addUser(T user);

    <T extends User> void updateUser(T user);

    void addFavourite(FavoriteMovie favoriteMovie);

    void removeFavourite(FavoriteMovie favoriteMovie);

    Role getRoleById(Long id);

    Boolean isFavorite(String userId, Long movieId);
}
