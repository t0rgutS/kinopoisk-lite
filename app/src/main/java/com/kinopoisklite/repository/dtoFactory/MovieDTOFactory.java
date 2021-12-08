package com.kinopoisklite.repository.dtoFactory;

import com.kinopoisklite.repository.Version;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;
import com.kinopoisklite.repository.php.model.RemoteMovieDTO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;

import java.time.LocalDateTime;

public class MovieDTOFactory {
    private static Version version;

    public static void init(Version version) {
        MovieDTOFactory.version = version;
    }

    public static Movie formUpdateMovieDTO(String title, String releaseYear, String duration,
                                           String genre, String country,
                                           String description, AgeRating rating, String coverUri,
                                           Movie initial) {
        Movie m;
        if (version == Version.PHP) {
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
            m.setDescription(description);
            m.setAgeRating(rating != null
                    ? rating
                    : initial.getAgeRating());
            ((RemoteMovieDTO) m).setRatingCategory(rating != null
                    ? rating.getRatingCategory()
                    : initial.getAgeRating().getRatingCategory());
            m.setCoverUri(coverUri);
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
            m.setGenre(genre != null ? !genre.isEmpty()
                    ? genre : initial.getGenre()
                    : initial.getGenre());
            m.setCountry(country != null ? !country.isEmpty()
                    ? country : initial.getCountry()
                    : initial.getCountry());
            m.setDescription(description);
            m.setAgeRating(rating != null
                    ? rating
                    : initial.getAgeRating());
            ((RoomMovieDTO) m).setAgeRatingId(rating != null
                    ? rating.getId()
                    : initial.getAgeRating().getId());
            m.setCoverUri(coverUri);
        }
        return m;
    }

    public static Movie formAddMovieDTO(String title, String releaseYear, String duration,
                                        String genre, String country,
                                        String description, AgeRating rating, String coverUri) {
        Movie m;
        if (version == Version.PHP) {
            m = new RemoteMovieDTO();
            m.setId(null);
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
            m.setGenre(genre);
            m.setCountry(country);
            m.setDescription(description);
            ((RoomMovieDTO) m).setAgeRatingId(rating.getId());
            m.setCoverUri(coverUri);
        }
        return m;
    }

    public static Movie formStoreMovieDTO(ServerMovieDTO movieDTO) {
        Movie movie = new RoomMovieDTO();
        movie.setId(movieDTO.getId());
        movie.setTitle(movieDTO.getTitle());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setDuration(movieDTO.getDuration());
        movie.setGenre(movieDTO.getGenre());
        movie.setCountry(movieDTO.getCountry());
        movie.setDescription(movieDTO.getDescription());
        ((RoomMovieDTO) movie).setAgeRatingId(movieDTO.getAgeRating().getId());
        movie.setCoverUri(movieDTO.getCoverUri());
        return movie;
    }
}
