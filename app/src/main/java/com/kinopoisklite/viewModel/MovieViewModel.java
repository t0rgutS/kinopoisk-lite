package com.kinopoisklite.viewModel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.dtoFactory.MovieDTOFactory;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;
import com.kinopoisklite.security.Actions;
import com.kinopoisklite.utility.CoverProvider;

import java.io.FileNotFoundException;
import java.util.List;

import lombok.Getter;

public class MovieViewModel extends ViewModel {
    @Getter
    private Movie savedMovie;

    private Boolean favorite;

    public void setSavedMovie(Movie savedMovie) {
        this.savedMovie = savedMovie;
        User sessionUser = ResourceManager.getSessionManager().getSessionUser();
        if (sessionUser != null)
            favorite = ResourceManager.getRepository().isFavorite(sessionUser.getId(), savedMovie.getId());
        else
            favorite = null;
    }

    public List<Actions> getAllowedActions() {
        return ResourceManager.getSessionManager().getAllowedActions();
    }

    public Boolean canAddToFav() {
        if (favorite == null)
            return false;
        return ResourceManager.getSessionManager()
                .getAllowedActions().contains(Actions.ADD_TO_FAV) && !favorite;
    }

    public LiveData<List<AgeRating>> getAgeRatings() {
        return ResourceManager.getRepository().getAgeRatings();
    }

    public Bitmap getCover(Activity parent, String coverUri) throws FileNotFoundException {
        return CoverProvider.getFromLocal(coverUri, parent);
    }

    public Bitmap getCover(String coverContent) {
        return CoverProvider.getFromServer(coverContent);
    }

    public void saveMovie(String title, String releaseYear, String duration,
                          String genre, String country,
                          String desctiption, AgeRating rating, String coverUri) throws PersistenceException {
        try {
            if (savedMovie == null)
                return;
            User user = ResourceManager.getSessionManager().getSessionUser();
            Movie movie = MovieDTOFactory.formUpdateMovieDTO(title, releaseYear, duration,
                    genre, country,
                    desctiption, rating, coverUri, savedMovie);
            if (!savedMovie.equals(movie)) {
                ResourceManager.getRepository().updateMovie(movie,
                        user != null ? user.getToken() : null);
                movie.setAgeRating(rating);
                movie.setCoverUri(coverUri);
                savedMovie = movie;
            }
        } catch (Exception e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    public void addMovie(String title, String releaseYear, String duration,
                         String genre, String country,
                         String desctiption, AgeRating rating, String coverUri) throws PersistenceException {
        try {
            User user = ResourceManager.getSessionManager().getSessionUser();
            Movie movie = MovieDTOFactory.formAddMovieDTO(title, releaseYear, duration,
                    genre, country,
                    desctiption, rating, coverUri);
            ResourceManager.getRepository().addMovie(movie,
                    user != null ? user.getToken() : null);
        } catch (Exception e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    public void addToFav() throws PersistenceException {
        if (favorite != null && !favorite) {
            try {
                User sessionUser = ResourceManager.getSessionManager().getSessionUser();
                if (sessionUser != null) {
                    FavoriteMovie favoriteMovie = new FavoriteMovie();
                    favoriteMovie.setUserId(sessionUser.getId());
                    favoriteMovie.setMovieId(savedMovie.getId());
                    favoriteMovie.setMovie(savedMovie);
                    favoriteMovie.setUser(sessionUser);
                    ResourceManager.getRepository().addFavourite(favoriteMovie);
                    favorite = true;
                }
            } catch (Exception e) {
                throw new PersistenceException(e.getMessage());
            }
        }
    }

    public LiveData<List<String>> getGenres() {
        return ResourceManager.getProvider().getGenres();
    }

    public LiveData<List<String>> getCountries() {
        return ResourceManager.getProvider().getCountries();
    }
}