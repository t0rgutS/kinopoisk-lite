package com.kinopoisklite.repository.dtoFactory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.Version;
import com.kinopoisklite.repository.network.model.ServerCoverDTO;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;
import com.kinopoisklite.repository.php.model.RemoteMovieDTO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.utility.CoverProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

public class MovieDTOFactory {
    private static Version version;
    private static Activity parent;

    public static void init(Activity parent, Version version) {
        MovieDTOFactory.parent = parent;
        MovieDTOFactory.version = version;
    }

    public static Movie formUpdateMovieDTO(String title, String releaseYear, String duration,
                                           String genre, String country,
                                           String description, AgeRating rating, String coverUri,
                                           Movie initial) {
        Movie m;
        if (version == Version.PHP) {
            m = new RemoteMovieDTO();
            m.setAgeRating(rating != null
                    ? rating
                    : initial.getAgeRating());
            ((RemoteMovieDTO) m).setRatingCategory(rating != null
                    ? rating.getRatingCategory()
                    : initial.getAgeRating().getRatingCategory());
            m.setCoverUri(coverUri);
        } else if (version == Version.ROOM) {
            m = new RoomMovieDTO();
            m.setAgeRating(rating != null
                    ? rating
                    : initial.getAgeRating());
            ((RoomMovieDTO) m).setAgeRatingId(rating != null
                    ? rating.getId()
                    : initial.getAgeRating().getId());
            m.setCoverUri(coverUri);
        } else {
            m = new ServerMovieDTO();
            m.setAgeRating(rating != null
                    ? rating
                    : initial.getAgeRating());
            ((ServerMovieDTO) m).setRatingCategory(rating != null
                    ? rating.getRatingCategory()
                    : initial.getAgeRating().getRatingCategory());
            try {
                ServerCoverDTO coverDTO = new ServerCoverDTO();
                Bitmap coverContent = CoverProvider.getFromLocal(coverUri, parent);
                if (coverContent != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    if (coverContent.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        coverDTO.setFileName(coverUri.contains("/")
                                ? coverUri.substring(coverUri.lastIndexOf("/") + 1)
                                : coverUri.substring(coverUri.lastIndexOf("\\") + 1));
                        coverDTO.setContent(Base64.encodeToString(outputStream.toByteArray(),
                                Base64.DEFAULT));
                        ((ServerMovieDTO) m).setCover(coverDTO);
                    }
                }
            } catch (FileNotFoundException fe) {
                fe.printStackTrace();
            }
        }
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
        return m;
    }

    public static Movie formAddMovieDTO(String title, String releaseYear, String duration,
                                        String genre, String country,
                                        String description, AgeRating rating, String coverUri) {
        Movie m;
        if (version == Version.PHP) {
            m = new RemoteMovieDTO();
            m.setId(null);
            ((RemoteMovieDTO) m).setRatingCategory(rating.getRatingCategory());
            m.setCoverUri(coverUri);
        } else if (version == Version.ROOM) {
            m = new RoomMovieDTO();
            ((RoomMovieDTO) m).setAgeRatingId(rating.getId());
            m.setCoverUri(coverUri);
        } else {
            m = new ServerMovieDTO();
            ((ServerMovieDTO) m).setRatingCategory(rating.getRatingCategory());
            try {
                ServerCoverDTO coverDTO = new ServerCoverDTO();
                Bitmap coverContent = CoverProvider.getFromLocal(coverUri, parent);
                if (coverContent != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    if (coverContent.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        coverDTO.setFileName(coverUri.contains("/")
                                ? coverUri.substring(coverUri.lastIndexOf("/") + 1)
                                : coverUri.substring(coverUri.lastIndexOf("\\") + 1));
                        coverDTO.setContent(Base64.encodeToString(outputStream.toByteArray(),
                                Base64.DEFAULT));
                        ((ServerMovieDTO) m).setCover(coverDTO);
                    }
                }
            } catch (FileNotFoundException fe) {
                fe.printStackTrace();
            }
        }
        m.setTitle(title);
        m.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
        m.setDuration(duration != null ? Integer.parseInt(duration) : 0);
        m.setGenre(genre);
        m.setCountry(country);
        m.setDescription(description);
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
