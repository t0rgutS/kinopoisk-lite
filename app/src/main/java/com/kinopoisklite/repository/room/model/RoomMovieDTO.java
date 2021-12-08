package com.kinopoisklite.repository.room.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "movies")
public class RoomMovieDTO extends Movie {
    @ColumnInfo(name = "age_rating_id")
    @NotNull
    private String ageRatingId;
}