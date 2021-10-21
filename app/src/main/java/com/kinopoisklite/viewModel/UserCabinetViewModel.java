package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.dtoFactory.UserDTOFactory;

import java.util.List;

public class UserCabinetViewModel extends ViewModel {

    public User getSessionUser() {
        return ResourceManager.getSessionManager().getSessionUser();
    }

    public LiveData<List<Movie>> getFavourites() {
        String userId = ResourceManager.getSessionManager().getSessionUser().getId();
        return ResourceManager.getRepository().getUserFavourites(userId);
    }

    public void removeFavourite(Movie movie) {
        String userId = ResourceManager.getSessionManager().getSessionUser().getId();
        FavoriteMovie fav = new FavoriteMovie();
        fav.setUserId(userId);
        fav.setMovieId(movie.getId());
        ResourceManager.getRepository().removeFavourite(fav);
    }

    public void updateUser(String firstName, String lastName) {
        User user = ResourceManager.getSessionManager().getSessionUser();
        if (!user.getExternal()) {
            if (!user.getFirstName().equals(firstName) && !user.getLastName().equals(lastName)) {
                ResourceManager.getRepository().updateUser(
                        UserDTOFactory.formUpdateUserDTO(
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