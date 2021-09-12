package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.dto.MovieDTO;
import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.repository.RepositoryManager;

import java.time.LocalDateTime;
import java.util.List;

public class MovieAddViewModel extends ViewModel {

    public LiveData<List<AgeRating>> getAgeRatings() {
        return RepositoryManager.getRepository().getAgeRatings();
    }

    public void addMovie(String title, String releaseYear, String duration,
                         String description, String ratingCategory) {
        MovieDTO newMovie = new MovieDTO();
        newMovie.setId(0L);
        newMovie.setTitle(title);
        newMovie.setReleaseYear(releaseYear != null ? Integer.parseInt(releaseYear) : LocalDateTime.now().getYear());
        newMovie.setDuration(duration != null ? Integer.parseInt(duration) : 0);
        newMovie.setDescription(description);
        newMovie.setRatingCategory(ratingCategory);
        RepositoryManager.getRepository().addMovie(newMovie);
    }
}