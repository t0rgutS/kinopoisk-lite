package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.Movie;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.security.Actions;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    public LiveData<List<Movie>> getAllMovies() {
        return ResourceManager.getRepository().getAllMovies();
    }

    public List<Actions> getAllowedActions() {
        return ResourceManager.getSessionManager().getAllowedActions();
    }

    public void deleteMovie(Movie movie) {
        ResourceManager.getRepository().deleteMovie(movie);
    }
}