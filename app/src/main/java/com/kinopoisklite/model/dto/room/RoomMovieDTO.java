package com.kinopoisklite.model.dto.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.model.entity.Movie;

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
    private Long ageRatingId;
}