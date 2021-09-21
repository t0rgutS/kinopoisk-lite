package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.repository.MovieDTOFactory;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.RepositoryManager;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MovieViewModel extends ViewModel {
    @Getter
    @Setter
    private Movie savedMovie;

    public LiveData<List<AgeRating>> getAgeRatings() {
        return RepositoryManager.getRepository().getAgeRatings();
    }

    public void upsertMovie(String title, String releaseYear, String duration,
                            String desctiption, AgeRating rating, String coverUri) throws PersistenceException {
        try {

            Movie movie;
            if (savedMovie != null) {
                movie = MovieDTOFactory.formMovieDTO(savedMovie.getId(), title, releaseYear, duration,
                        desctiption, rating, coverUri);
                RepositoryManager.getRepository().updateMovie(movie);
            } else {
                movie = MovieDTOFactory.formMovieDTO(title, releaseYear, duration,
                        desctiption, rating, coverUri);
                RepositoryManager.getRepository().addMovie(movie);
            }
        } catch (Exception e) {
            throw new PersistenceException(e.getMessage());
        }
    }
}