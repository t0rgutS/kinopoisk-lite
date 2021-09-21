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

    public static Movie formUpdateMovieDTO(String title, String releaseYear, String duration,
                                           String description, AgeRating rating, String coverUri,
                                           Movie initial) {
        Movie m;
        if (version == dtoVersion.PHP) {
            m = new RemoteMovieDTO();
            m.setId(initial.getId());
            m.setTitle(title != null ? !title.isEmpty()
                    ? title : initial.getTitle()
                    : initial.getTitle());
            m.setReleaseYear(releaseYear != null ?
                    !releaseYear.isEmpty() ? Integer.parseInt(releaseYear) : initial.getReleaseYear()
                    : initial.getReleaseYear());
            m.setDuration(duration != null
                    ? !duration.isEmpty() ? Integer.parseInt(duration) : initial.getDuration()
                    : initial.getDuration());
            m.setDescription(description != null ? description : initial.getDescription());
            ((RemoteMovieDTO) m).setRatingCategory(rating != null
                    ? rating.getRatingCategory()
                    : initial.getAgeRating().getRatingCategory());
            m.setCoverUri(coverUri != null
                    ? !coverUri.isEmpty() ? coverUri : initial.getCoverUri()
                    : initial.getCoverUri());
        } else {
            m = new RoomMovieDTO();
            m.setId(initial.getId());
            m.setTitle(title != null ? !title.isEmpty()
                    ? title : initial.getTitle()
                    : initial.getTitle());
            m.setReleaseYear(releaseYear != null ?
                    !releaseYear.isEmpty() ? Integer.parseInt(releaseYear) : initial.getReleaseYear()
                    : initial.getReleaseYear());
            m.setDuration(duration != null
                    ? !duration.isEmpty() ? Integer.parseInt(duration) : initial.getDuration()
                    : initial.getDuration());
            m.setDescription(description != null ? description : initial.getDescription());
            ((RoomMovieDTO) m).setAgeRatingId(rating != null
                    ? rating.getId()
                    : initial.getAgeRating().getId());
            m.setCoverUri(coverUri != null
                    ? !coverUri.isEmpty() ? coverUri : initial.getCoverUri()
                    : initial.getCoverUri());
        }
        return m;
    }

    public static Movie formAddMovieDTO(String title, String releaseYear, String duration,
                                        String description, AgeRating rating, String coverUri) {
        Movie m;
        if (version == dtoVersion.PHP) {
            m = new RemoteMovieDTO();
            m.setId(0L);
            m.setTitle(title);
            m.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
            m.setDuration(duration != null ? Integer.parseInt(duration) : 0);
            m.setDescription(description);
            ((RemoteMovieDTO) m).setRatingCategory(rating.getRatingCategory());
            m.setCoverUri(coverUri);
        } else {
            m = new RoomMovieDTO();
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
