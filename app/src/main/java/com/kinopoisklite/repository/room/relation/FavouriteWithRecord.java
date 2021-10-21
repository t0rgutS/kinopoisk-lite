package com.kinopoisklite.repository.room.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavouriteWithRecord {
    @Embedded
    private FavoriteMovie favoriteMovie;

    @Relation(parentColumn = "movie_id", entityColumn = "id", entity = RoomMovieDTO.class)
    private MovieWithRating movieWithRating;
}
