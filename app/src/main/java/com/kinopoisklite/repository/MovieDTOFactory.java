package com.kinopoisklite.repository;

import com.kinopoisklite.repository.remote.model.RemoteMovieDTO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;

import java.time.LocalDateTime;

public class MovieDTOFactory {
    private static dtoVersion version;

    public static void init(dtoVersion version) {
        MovieDTOFactory.version = version;
    }

    public static Movie formMovieDTO(String title, String releaseYear, String duration,
                                     String description, AgeRating rating, String coverUri) {
        return formMovieDTO(null, title, releaseYear, duration, description, rating, coverUri);
    }

    public static Movie formMovieDTO(Long id, String title, String releaseYear, String duration,
                                     String description, AgeRating rating, String coverUri) {
        Movie m;
        if (version == dtoVersion.PHP) {
            m = new RemoteMovieDTO();
            m.setId(id != null ? id : 0L);
            m.setTitle(title);
            m.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
            m.setDuration(duration != null ? Integer.parseInt(duration) : 0);
            m.setDescription(description);
            ((RemoteMovieDTO) m).setRatingCategory(rating.getRatingCategory());
            m.setCoverUri(coverUri);
        } else {
            m = new RoomMovieDTO();
            if (id != null)
                m.setId(id);
            m.setTitle(title);
            m.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
            m.setDuration(duration != null ? Integer.parseInt(duration) : 0);
            m.setDescription(description);
            ((RoomMovieDTO) m).setAgeRatingId(rating.getId());
            m.setCoverUri(coverUri);
        }
        return m;
    }
}
