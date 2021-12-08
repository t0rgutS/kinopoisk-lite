package com.kinopoisklite.repository.network;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.Token;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.Repository;
import com.kinopoisklite.repository.dtoFactory.MovieDTOFactory;
import com.kinopoisklite.repository.network.api.AgeRatingApi;
import com.kinopoisklite.repository.network.api.MovieApi;
import com.kinopoisklite.repository.network.api.UserApi;
import com.kinopoisklite.repository.network.model.ServerMovieDTO;
import com.kinopoisklite.repository.room.MovieRoomDatabase;
import com.kinopoisklite.repository.room.dao.AgeRatingDAO;
import com.kinopoisklite.repository.room.dao.MovieDAO;
import com.kinopoisklite.repository.room.dao.TokenDAO;
import com.kinopoisklite.repository.room.dao.UserDAO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerRepository implements Repository {
    private MovieApi movieApi;
    private AgeRatingApi ageRatingApi;
    private UserApi userApi;
    private MutableLiveData<List<Movie>> favoriteMovies;
    private MutableLiveData<List<AgeRating>> ageRatings;
    private AgeRatingDAO ageRatingDAO;
    private UserDAO userDAO;
    private TokenDAO tokenDAO;
    private MovieDAO movieDAO;

    public ServerRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        ageRatingDAO = database.ageRatingDAO();
        userDAO = database.userDAO();
        tokenDAO = database.tokenDAO();
        movieDAO = database.movieDAO();
        ageRatings = new MutableLiveData<>();
        favoriteMovies = new MutableLiveData<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieApi = retrofit.create(MovieApi.class);
        ageRatingApi = retrofit.create(AgeRatingApi.class);
        userApi = retrofit.create(UserApi.class);
    }

    @Override
    public LiveData<List<ServerMovieDTO>> getAllMovies() {
        MutableLiveData<List<ServerMovieDTO>> movies = new MutableLiveData<>();
        movieApi.getAllMovies().enqueue(new Callback<List<ServerMovieDTO>>() {
            @Override
            public void onResponse(Call<List<ServerMovieDTO>> call, Response<List<ServerMovieDTO>> response) {
                if (response.isSuccessful() && response.body() != null)
                    movies.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ServerMovieDTO>> call, Throwable t) {

            }
        });
        return movies;
    }

    @Override
    public LiveData<List<Movie>> getUserFavourites(User user) {
        if (favoriteMovies.getValue() != null)
            favoriteMovies.setValue(null);
        movieApi.getFavoriteMovies(user.getToken().getAccessToken()).enqueue(new Callback<List<ServerMovieDTO>>() {
            @Override
            public void onResponse(Call<List<ServerMovieDTO>> call, Response<List<ServerMovieDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        response.body().forEach(movie -> movieDAO.addMovie(
                                (RoomMovieDTO) MovieDTOFactory.formStoreMovieDTO(movie)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    favoriteMovies.setValue(response.body().stream()
                            .map(Movie.class::cast).collect(Collectors.toList()));
                }
            }

            @Override
            public void onFailure(Call<List<ServerMovieDTO>> call, Throwable t) {

            }
        });
        return favoriteMovies;
    }

    @Override
    public LiveData<List<AgeRating>> getAgeRatings() {
        if (ageRatings.getValue() == null || (ageRatings.getValue() == null
                ? true : ageRatings.getValue().size() == 0)) {
            ageRatingApi.getAllRatings().enqueue(new Callback<List<AgeRating>>() {
                @Override
                public void onResponse(Call<List<AgeRating>> call, Response<List<AgeRating>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ageRatings.setValue(response.body());
                        ageRatingDAO.addAgeRatings(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<AgeRating>> call, Throwable t) {

                }
            });
        }
        return ageRatings;
    }

    @Override
    public <T extends Movie> void deleteMovie(T movie, Token token) {
        movieApi.deleteMovie(movie.getId(), token.getAccessToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public <T extends Movie> void updateMovie(T movie, Token token) {
        movieApi.updateMovie(movie.getId(), token.getAccessToken(),
                (ServerMovieDTO) movie).enqueue(new Callback<ServerMovieDTO>() {
            @Override
            public void onResponse(Call<ServerMovieDTO> call, Response<ServerMovieDTO> response) {

            }

            @Override
            public void onFailure(Call<ServerMovieDTO> call, Throwable t) {

            }
        });
    }

    @Override
    public <T extends Movie> void addMovie(T movie, Token token) {
        movieApi.createMovie(token.getAccessToken(), (ServerMovieDTO) movie)
                .enqueue(new Callback<ServerMovieDTO>() {
                    @Override
                    public void onResponse(Call<ServerMovieDTO> call, Response<ServerMovieDTO> response) {

                    }

                    @Override
                    public void onFailure(Call<ServerMovieDTO> call, Throwable t) {

                    }
                });
    }

    @Override
    public User getUserById(String id) {
        User user = userDAO.getUserById(id);
        return user;
    }

    @Override
    public <T extends User> LiveData<T> getUserByLogin(String login) {
        //Not implemented
        return null;
    }

    @Override
    public <T extends User> void addUser(T user) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            userDAO.addUser(user);
        });
    }

    @Override
    public <T extends User> void updateUser(T user) {
        userApi.updateUserInfo(user.getToken().getAccessToken(),
                new HashMap<String, String>() {{
                    put("firstName", user.getFirstName());
                    put("lastName", user.getLastName());
                }}).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void addFavourite(FavoriteMovie favoriteMovie) {
        movieApi.addToFav(favoriteMovie.getMovieId(),
                favoriteMovie.getUser().getToken().getAccessToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    List<Movie> currentFavs = favoriteMovies.getValue();
                    currentFavs.add(favoriteMovie.getMovie());
                    favoriteMovies.setValue(currentFavs);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void removeFavourite(FavoriteMovie favoriteMovie) {
        movieApi.removeFromFav(favoriteMovie.getMovieId(),
                favoriteMovie.getUser().getToken().getAccessToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    List<Movie> currentFavs = favoriteMovies.getValue();
                    currentFavs.remove(favoriteMovie.getMovie());
                    favoriteMovies.setValue(currentFavs);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public Boolean isFavorite(String userId, String movieId) {
        if (favoriteMovies.getValue() == null)
            return false;
        List<Movie> currentFavorites = favoriteMovies.getValue();
        return currentFavorites.stream().anyMatch(movie -> movie.getId().equals(movieId));
    }

    @Override
    public void addToken(Token token) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            tokenDAO.addToken(token);
        });
    }

    @Override
    public void deleteToken(Token token) {
        MovieRoomDatabase.getExecutorService().execute(() -> {
            tokenDAO.deleteToken(token);
        });
    }

    @Override
    public Token getTokenByUserId(String userId) {
        return tokenDAO.getByUserId(userId);
    }
}