package com.kinopoisklite.repository.room;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.Role;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.Repository;
import com.kinopoisklite.repository.room.dao.AgeRatingDAO;
import com.kinopoisklite.repository.room.dao.MovieDAO;
import com.kinopoisklite.repository.room.dao.RoleDAO;
import com.kinopoisklite.repository.room.dao.UserDAO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.repository.room.model.RoomUserDTO;
import com.kinopoisklite.repository.room.relation.UserWithRole;

import java.util.List;
import java.util.stream.Collectors;

public class RoomRepository implements Repository {
    private MovieDAO movieDAO;
    private AgeRatingDAO ageRatingDAO;
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private LiveData<List<RoomMovieDTO>> movies;
    private LiveData<List<AgeRating>> ageRatings;

    public RoomRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        movieDAO = database.movieDAO();
        ageRatingDAO = database.ageRatingDAO();
        userDAO = database.userDAO();
        roleDAO = database.roleDAO();
        movies = Transformations.map(movieDAO.getAllMovies(), relList -> relList.stream().map(rel -> {
            RoomMovieDTO m = rel.getRoomMovieDTO();
            m.setAgeRating(rel.getAgeRating());
            return m;
        }).collect(Collectors.toList()));
        ageRatings = ageRatingDAO.getAgeRatings();
    }

    @Override
    public LiveData<List<RoomMovieDTO>> getAllMovies() {
        return movies;
    }

    @Override
    public LiveData<List<Movie>> getUserFavourites(String id) {
        return Transformations.map(movieDAO.getUserFavourites(id), relList -> relList.stream().map(rel -> {
            Movie m = rel.getMovieWithRating().getRoomMovieDTO();
            m.setAgeRating(rel.getMovieWithRating().getAgeRating());
            return m;
        }).collect(Collectors.toList()));
    }

    @Override
    public LiveData<List<AgeRating>> getAgeRatings() {
        return ageRatings;
    }

    @Override
    public <T extends Movie> void deleteMovie(T movie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.deleteMovie((RoomMovieDTO) movie);
        });
    }

    @Override
    public <T extends Movie> void addMovie(T movie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.addMovie((RoomMovieDTO) movie);
        });
    }

    @Override
    public RoomUserDTO getUserById(String id) {
        UserWithRole userWithRole = userDAO.getUserById(id);
        Role role = userWithRole.getRole();
        RoomUserDTO user = userWithRole.getRoomUserDTO();
        user.setRole(role);
        return user;
    }

    @Override
    public LiveData<RoomUserDTO> getUserByLogin(String login) {
        return Transformations.map(userDAO.getUserByLogin(login), userWithRole -> {
            RoomUserDTO userDTO = userWithRole.getRoomUserDTO();
            userDTO.setRole(userWithRole.getRole());
            return userDTO;
        });
    }

    @Override
    public <T extends User> void addUser(T user) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            RoomUserDTO userDTO = (RoomUserDTO) user;
            //TODO password encryption
            // userDTO.setPassword();
            userDAO.addUser(userDTO);
        });
    }

    @Override
    public <T extends User> void updateUser(T user) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            userDAO.updateUser((RoomUserDTO) user);
        });
    }

    @Override
    public void addFavourite(FavoriteMovie favoriteMovie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.addFavourite(favoriteMovie);
        });
    }

    @Override
    public void removeFavourite(FavoriteMovie favoriteMovie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.removeFavourite(favoriteMovie);
        });
    }

    @Override
    public Role getRoleById(Long id) {
        return roleDAO.getRoleById(id);
    }

    @Override
    public Boolean isFavorite(String userId, Long movieId) {
        return movieDAO.isFavorite(userId, movieId);
    }

    @Override
    public <T extends Movie> void updateMovie(T movie) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            movieDAO.updateMovie((RoomMovieDTO) movie);
        });
    }
}
