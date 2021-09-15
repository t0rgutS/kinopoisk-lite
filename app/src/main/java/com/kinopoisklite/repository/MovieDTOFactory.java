package com.kinopoisklite.repository;

import com.kinopoisklite.repository.remote.model.RemoteMovieDTO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.model.Movie;

import java.time.LocalDateTime;

public class MovieDTOFactory {
    private static dtoVersion version;

    public static void init(dtoVersion version) {
        MovieDTOFactory.version = version;
    }

    public static Movie formMovieDTO(String title, String releaseYear, String duration,
                                     String description, AgeRating rating) {
        Movie m;
        if (version == dtoVersion.PHP) {
            m = new RemoteMovieDTO();
            m.setId(0L);
            m.setTitle(title);
            m.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
            m.setDuration(duration != null ? Integer.parseInt(duration) : 0);
            m.setDescription(description);
            ((RemoteMovieDTO) m).setRatingCategory(rating.getRatingCategory());
        } else {
            m = new RoomMovieDTO();
            m.setTitle(title);
            m.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
            m.setDuration(duration != null ? Integer.parseInt(duration) : 0);
            m.setDescription(description);
            ((RoomMovieDTO) m).setAgeRatingId(rating.getId());
        }
        return m;
    }
}
