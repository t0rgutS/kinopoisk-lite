package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.dtoFactory.UserRequestFactory;

import java.util.List;

public class UserCabinetViewModel extends ViewModel {

    public User getSessionUser() {
        return ResourceManager.getSessionManager().getSessionUser();
    }

    public LiveData<List<Movie>> getFavourites() {
        User user = ResourceManager.getSessionManager().getSessionUser();
        if (user.getFavoriteMovies() == null)
            user.setFavoriteMovies(ResourceManager.getRepository().getUserFavourites(user));
        return user.getFavoriteMovies();
    }

    public void removeFavourite(Movie movie) {
        User user = ResourceManager.getSessionManager().getSessionUser();
        FavoriteMovie fav = new FavoriteMovie();
        fav.setUser(user);
        fav.setUserId(user.getId());
        fav.setMovieId(movie.getId());
        fav.setMovie(movie);
        ResourceManager.getRepository().removeFavourite(fav);
    }

    public void updateUser(String firstName, String lastName) {
        User user = ResourceManager.getSessionManager().getSessionUser();
        if (!user.getExternal()) {
            if (!user.getFirstName().equals(firstName) && !user.getLastName().equals(lastName)) {
                ResourceManager.getRepository().updateUser(
                        UserRequestFactory.formUpdateUserRequest(
                                user.getId(), user.getLogin(), firstName, lastName,
                                user.getPassword(), user.getExternal(), user.getRole(), user)
                );
            }

        }
    }

    public void logout() {
        ResourceManager.getSessionManager().logout();
    }

}