package com.kinopoisklite.viewModel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.repository.MovieDTOFactory;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.ResourceManager;

import java.io.FileNotFoundException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MovieViewModel extends ViewModel {
    @Getter
    @Setter
    private Movie savedMovie;

    public LiveData<List<AgeRating>> getAgeRatings() {
        return ResourceManager.getRepository().getAgeRatings();
    }

    public Bitmap getCover(Activity parent, String coverUri) throws FileNotFoundException {
        if (coverUri == null)
            return null;
        if (coverUri.isEmpty())
            return null;
        return BitmapFactory.decodeFileDescriptor(
                parent.getApplicationContext()
                        .getContentResolver().
                        openFileDescriptor(
                                Uri.parse(coverUri), "r")
                        .getFileDescriptor());
    }

    public void saveMovie(String title, String releaseYear, String duration,
                          String desctiption, AgeRating rating, String coverUri) throws PersistenceException {
        try {
            if (savedMovie == null)
                return;
            Movie movie = MovieDTOFactory.formUpdateMovieDTO(title, releaseYear, duration,
                    desctiption, rating, coverUri, savedMovie);
            if (!savedMovie.equals(movie)) {
                ResourceManager.getRepository().updateMovie(movie);
                movie.setAgeRating(rating);
                savedMovie = movie;
            }
        } catch (Exception e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    public void addMovie(String title, String releaseYear, String duration,
                         String desctiption, AgeRating rating, String coverUri) throws PersistenceException {
        try {
            Movie movie = MovieDTOFactory.formAddMovieDTO(title, releaseYear, duration,
                    desctiption, rating, coverUri);
            ResourceManager.getRepository().addMovie(movie);
        } catch (Exception e) {
            throw new PersistenceException(e.getMessage());
        }
    }
}