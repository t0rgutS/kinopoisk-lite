package com.kinopoisklite.repository.room.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.model.entity.AgeRating;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieWithRating {
    @Embedded
    private RoomMovieDTO roomMovieDTO;

    @Relation(parentColumn = "age_rating_id", entityColumn = "id", entity = AgeRating.class)
    private AgeRating ageRating;
}
