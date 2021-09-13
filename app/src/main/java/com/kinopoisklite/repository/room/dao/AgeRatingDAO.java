package com.kinopoisklite.repository.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kinopoisklite.model.entity.AgeRating;

import java.util.List;

@Dao
public interface AgeRatingDAO {
    @Query("SELECT * FROM age_ratings")
    LiveData<List<AgeRating>> getAgeRatings();

    @Query("SELECT EXISTS(SELECT * FROM age_ratings WHERE id = :id)")
    Boolean isRatingExists(Long id);

    @Insert
    void addAgeRating(AgeRating rating);
}
