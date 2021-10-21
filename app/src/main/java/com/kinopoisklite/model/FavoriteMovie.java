package com.kinopoisklite.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "favorite_movies", primaryKeys = {"user_id", "movie_id"})
public class FavoriteMovie {
    @ColumnInfo(name = "user_id")
    @NotNull
    private String userId;

    @ColumnInfo(name = "movie_id")
    @NotNull
    private Long movieId;

    @Ignore
    private Movie movie;
}
