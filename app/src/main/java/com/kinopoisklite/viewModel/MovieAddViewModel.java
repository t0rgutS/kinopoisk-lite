package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.repository.MovieDTOFactory;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.RepositoryManager;

import java.util.List;

public class MovieAddViewModel extends ViewModel {

    public LiveData<List<AgeRating>> getAgeRatings() {
        return RepositoryManager.getRepository().getAgeRatings();
    }

    public void addMovie(String title, String releaseYear, String duration,
                         String description, AgeRating rating) throws PersistenceException {
        try {
            Movie newMovie = MovieDTOFactory.formMovieDTO(title, releaseYear, duration,
                    description, rating);
            RepositoryManager.getRepository().addMovie(newMovie);
        } catch (Exception e) {
            throw new PersistenceException(e.getMessage());
        }
    }
}