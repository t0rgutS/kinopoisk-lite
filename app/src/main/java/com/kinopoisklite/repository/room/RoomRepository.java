package com.kinopoisklite.repository.room;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kinopoisklite.model.dto.room.RoomMovieDTO;
import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.model.entity.Movie;
import com.kinopoisklite.repository.Repository;
import com.kinopoisklite.repository.room.dao.AgeRatingDAO;
import com.kinopoisklite.repository.room.dao.MovieDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomRepository implements Repository {
    private MovieDAO movieDAO;
    private AgeRatingDAO ageRatingDAO;
    private LiveData<List<RoomMovieDTO>> movies;
    private LiveData<List<AgeRating>> ageRatings;

    public RoomRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        movieDAO = database.movieDAO();
        ageRatingDAO = database.ageRatingDAO();
        movies = Transformations.map(movieDAO.getAllMovies(), relList -> relList.stream().map(rel -> {
            RoomMovieDTO m = rel.getRoomMovieDTO();
            m.setAgeRating(rel.getAgeRating());
            return m;
        }).collect(Collectors.toCollection(ArrayList::new)));
        ageRatings = ageRatingDAO.getAgeRatings();
    }

    @Override
    public LiveData<List<RoomMovieDTO>> getAllMovies() {
        return movies;
    }

    @Override
    public LiveData<List<AgeRating>> getAgeRatings() {
        return ageRatings;
    }

    @Override
    public <T extends Movie> void deleteMovie(T movie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.deleteMovie((RoomMovieDTO) movie);
        });
    }

    @Override
    public <T extends Movie> void addMovie(T movie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.addMovie((RoomMovieDTO) movie);
        });
    }
}
