package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.entity.Movie;
import com.kinopoisklite.repository.RepositoryManager;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    public LiveData<List<Movie>> getAllMovies() {
        return RepositoryManager.getRepository().getAllMovies();
    }

    public void deleteMovie(Movie movie) {
        RepositoryManager.getRepository().deleteMovie(movie);
    }
}