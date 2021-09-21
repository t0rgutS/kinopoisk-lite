package com.kinopoisklite.repository.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.repository.room.relation.MovieWithRating;

import java.util.List;

@Dao
public interface MovieDAO {
    @Transaction
    @Query("SELECT * FROM movies")
    LiveData<List<MovieWithRating>> getAllMovies();

    @Insert
    void addMovie(RoomMovieDTO movie);

    @Update
    void updateMovie(RoomMovieDTO movie);

    @Delete
    void deleteMovie(RoomMovieDTO movie);
}
