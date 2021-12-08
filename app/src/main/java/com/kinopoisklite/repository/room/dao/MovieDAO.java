package com.kinopoisklite.repository.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.repository.room.relation.FavouriteWithRecord;
import com.kinopoisklite.repository.room.relation.MovieWithRating;

import java.util.List;

@Dao
public interface MovieDAO {
    @Transaction
    @Query("SELECT * FROM movies")
    LiveData<List<MovieWithRating>> getAllMovies();

    @Transaction
    @Query("SELECT * FROM favorite_movies WHERE user_id=:id")
    LiveData<List<FavouriteWithRecord>> getUserFavourites(String id);

    @Query("SELECT EXISTS (SELECT * FROM favorite_movies WHERE user_id=:userId AND movie_id=:movieId)")
    Boolean isFavorite(String userId, String movieId);

    @Insert
    void addFavourite(FavoriteMovie favoriteMovie);

    @Delete
    void removeFavourite(FavoriteMovie favoriteMovie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMovie(RoomMovieDTO movie);

    @Update
    void updateMovie(RoomMovieDTO movie);

    @Delete
    void deleteMovie(RoomMovieDTO movie);
}
