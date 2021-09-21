package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.repository.MovieDTOFactory;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.ResourceManager;

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

    public void saveMovie(String title, String releaseYear, String duration,
                          String desctiption, AgeRating rating, String coverUri) throws PersistenceException {
        try {
            if (savedMovie == null)
                return;
            Movie movie = MovieDTOFactory.formUpdateMovieDTO(title, releaseYear, duration,
                    desctiption, rating, coverUri, savedMovie);
            if (!savedMovie.equals(movie))
                ResourceManager.getRepository().updateMovie(movie);
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