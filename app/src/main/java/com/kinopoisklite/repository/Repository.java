package com.kinopoisklite.repository;

import androidx.lifecycle.LiveData;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.Token;
import com.kinopoisklite.model.User;

import java.util.List;

public interface Repository {
    <T extends Movie> LiveData<List<T>> getAllMovies();

    LiveData<List<Movie>> getUserFavourites(User user);

    LiveData<List<AgeRating>> getAgeRatings();

    <T extends Movie> void deleteMovie(T movie, Token token);

    <T extends Movie> void updateMovie(T movie, Token token);

    <T extends Movie> void addMovie(T movie, Token token);

    <T extends User> T getUserById(String id);

    <T extends User> LiveData<T> getUserByLogin(String login);

    <T extends User> void addUser(T user);

    <T extends User> void updateUser(T user);

    void addFavourite(FavoriteMovie favoriteMovie);

    void removeFavourite(FavoriteMovie favoriteMovie);

    Boolean isFavorite(String userId, String movieId);

    void addToken(Token token);

    void deleteToken(Token token);

    Token getTokenByUserId(String userId);

}
