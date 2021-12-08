package com.kinopoisklite.repository.room;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.Token;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.Repository;
import com.kinopoisklite.repository.room.dao.AgeRatingDAO;
import com.kinopoisklite.repository.room.dao.MovieDAO;
import com.kinopoisklite.repository.room.dao.UserDAO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;

import java.util.List;
import java.util.stream.Collectors;

public class RoomRepository implements Repository {
    private MovieDAO movieDAO;
    private AgeRatingDAO ageRatingDAO;
    private UserDAO userDAO;
    private LiveData<List<RoomMovieDTO>> movies;
    private LiveData<List<AgeRating>> ageRatings;

    public RoomRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        movieDAO = database.movieDAO();
        ageRatingDAO = database.ageRatingDAO();
        userDAO = database.userDAO();
        movies = Transformations.map(movieDAO.getAllMovies(), relList -> relList.stream().map(rel -> {
            RoomMovieDTO m = rel.getRoomMovieDTO();
            m.setAgeRating(rel.getAgeRating());
            return m;
        }).collect(Collectors.toList()));
        ageRatings = ageRatingDAO.getAgeRatings();
    }

    @Override
    public LiveData<List<RoomMovieDTO>> getAllMovies() {
        return movies;
    }

    @Override
    public LiveData<List<Movie>> getUserFavourites(User user) {
        return Transformations.map(movieDAO.getUserFavourites(user.getId()), relList -> relList.stream().map(rel -> {
            Movie m = rel.getMovieWithRating().getRoomMovieDTO();
            m.setAgeRating(rel.getMovieWithRating().getAgeRating());
            return m;
        }).collect(Collectors.toList()));
    }

    @Override
    public LiveData<List<AgeRating>> getAgeRatings() {
        return ageRatings;
    }

    @Override
    public <T extends Movie> void deleteMovie(T movie, Token token) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.deleteMovie((RoomMovieDTO) movie);
        });
    }

    @Override
    public <T extends Movie> void addMovie(T movie, Token token) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.addMovie((RoomMovieDTO) movie);
        });
    }

    @Override
    public User getUserById(String id) {
        User user = userDAO.getUserById(id);
        return user;
    }

    @Override
    public LiveData<User> getUserByLogin(String login) {
        return userDAO.getUserByLogin(login);
    }

    @Override
    public <T extends User> void addUser(T user) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            userDAO.addUser(user);
        });
    }

    @Override
    public <T extends User> void updateUser(T user) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            userDAO.updateUser(user);
        });
    }

    @Override
    public void addFavourite(FavoriteMovie favoriteMovie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.addFavourite(favoriteMovie);
        });
    }

    @Override
    public void removeFavourite(FavoriteMovie favoriteMovie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.removeFavourite(favoriteMovie);
        });
    }

    @Override
    public Boolean isFavorite(String userId, String movieId) {
        return movieDAO.isFavorite(userId, movieId);
    }

    @Override
    public void addToken(Token token) {
        //Not implemented
    }

    @Override
    public void deleteToken(Token token) {
        //Not implemented
    }

    @Override
    public Token getTokenByUserId(String userId) {
        //Not implemented
        return null;
    }

    @Override
    public <T extends Movie> void updateMovie(T movie, Token token) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.updateMovie((RoomMovieDTO) movie);
        });
    }
}
