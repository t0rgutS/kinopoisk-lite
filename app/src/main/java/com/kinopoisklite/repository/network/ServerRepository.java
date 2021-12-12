package com.kinopoisklite.repository.network;

import android.app.Application;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kinopoisklite.exception.PersistenceException;
import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Movie;
import com.kinopoisklite.model.Token;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.Repository;
import com.kinopoisklite.repository.ResourceManager;
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
import com.kinopoisklite.security.network.api.ServerTokenService;
import com.kinopoisklite.utility.CoverProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
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
    private ServerTokenService tokenService;
    private LiveData<List<Movie>> favoriteMovies;
    private LiveData<List<AgeRating>> ageRatings;
    private AgeRatingDAO ageRatingDAO;
    private UserDAO userDAO;
    private TokenDAO tokenDAO;
    private MovieDAO movieDAO;
    private File imagesDir;

    public ServerRepository(Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getDatabase(application);
        ageRatingDAO = database.ageRatingDAO();
        userDAO = database.userDAO();
        tokenDAO = database.tokenDAO();
        movieDAO = database.movieDAO();
        ageRatings = ageRatingDAO.getAgeRatings();
        favoriteMovies = new MutableLiveData<>();
        imagesDir = new ContextWrapper(application.getApplicationContext())
                .getDir("Images", ContextWrapper.MODE_PRIVATE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        movieApi = retrofit.create(MovieApi.class);
        ageRatingApi = retrofit.create(AgeRatingApi.class);
        userApi = retrofit.create(UserApi.class);
        tokenService = retrofit.create(ServerTokenService.class);
    }

    @Override
    public LiveData<List<ServerMovieDTO>> getAllMovies() {
        MutableLiveData<List<ServerMovieDTO>> movies = new MutableLiveData<>();
        movieApi.getAllMovies().enqueue(new Callback<List<ServerMovieDTO>>() {
            @Override
            public void onResponse(Call<List<ServerMovieDTO>> call, Response<List<ServerMovieDTO>> response) {
                if (response.isSuccessful() && response.body() != null)
                    movies.setValue(response.body());
                else if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка получения фильмов: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка получения фильмов: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ServerMovieDTO>> call, Throwable t) {

            }
        });
        return movies;
    }

    @Override
    public LiveData<List<Movie>> getUserFavourites(User user) {
        movieApi.getFavoriteMovies("Bearer " +
                user.getToken().getAccessToken()).enqueue(new Callback<List<ServerMovieDTO>>() {
            @Override
            public void onResponse(Call<List<ServerMovieDTO>> call, Response<List<ServerMovieDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        response.body().forEach(movie -> {
                            if (movie.getCover() != null) {
                                if (movie.getCover().getContent() != null) {
                                    Bitmap cover = CoverProvider.getFromServer(movie.getCover().getContent());
                                    if (cover != null) {
                                        String filename = movie.getCover().getFileName() != null
                                                ? movie.getCover().getFileName()
                                                : String.format("image_%s.png", Instant.now().toEpochMilli());
                                        try {
                                            File imageFile = new File(imagesDir, filename);
                                            OutputStream stream = new FileOutputStream(imageFile);
                                            cover.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            stream.flush();
                                            stream.close();
                                            movie.setCoverUri(Uri.fromFile(imageFile).toString());
                                        } catch (IOException ioe) {
                                            ioe.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (!movieDAO.isMovieExists(movie.getId()))
                                movieDAO.addMovie(
                                        (RoomMovieDTO) MovieDTOFactory.formStoreMovieDTO(movie));
                            if (!movieDAO.isFavoriteExists(user.getId(), movie.getId())) {
                                FavoriteMovie fav = new FavoriteMovie();
                                fav.setUserId(user.getId());
                                fav.setMovieId(movie.getId());
                                movieDAO.addFavourite(fav);
                            }
                        });
                        if (response.body().size() > 0) {
                            List<String> favIdList = response.body().stream()
                                    .map(Movie::getId).collect(Collectors.toList());
                            movieDAO.removeExtraFavorites(user.getId(), favIdList);
                        } else
                            movieDAO.removeUsersFavorites(user.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка получения списка изобранных: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка получения списка избранных: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ServerMovieDTO>> call, Throwable t) {
            }
        });
        favoriteMovies = (Transformations.map(movieDAO.getUserFavourites(user.getId()),
                relList -> relList.stream().map(rel -> {
                    Movie m = rel.getMovieWithRating().getRoomMovieDTO();
                    m.setAgeRating(rel.getMovieWithRating().getAgeRating());
                    return m;
                }).collect(Collectors.toList())));
        return favoriteMovies;
    }

    @Override
    public LiveData<List<AgeRating>> getAgeRatings() {
        ageRatingApi.getAllRatings().enqueue(new Callback<List<AgeRating>>() {
            @Override
            public void onResponse(Call<List<AgeRating>> call, Response<List<AgeRating>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    response.body().forEach(rating ->
                    {
                        if (!ageRatingDAO.isRatingExists(rating.getId()))
                            ageRatingDAO.addAgeRating(rating);
                    });
                } else if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка получения возрастных рейтингов: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка получения возрастных рейтингов: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AgeRating>> call, Throwable t) {

            }
        });
        return ageRatings;
    }

    @Override
    public <T extends Movie> void deleteMovie(T movie, Token token) {
        movieApi.deleteMovie(movie.getId(), "Bearer " +
                token.getAccessToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка удаления фильма: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка удаления фильма: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public <T extends Movie> void updateMovie(T movie, Token token) {
        movieApi.updateMovie(movie.getId(), "Bearer " +
                        token.getAccessToken(),
                (ServerMovieDTO) movie).enqueue(new Callback<ServerMovieDTO>() {
            @Override
            public void onResponse(Call<ServerMovieDTO> call, Response<ServerMovieDTO> response) {
                if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка обновления фильма: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка обновления фильма: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerMovieDTO> call, Throwable t) {

            }
        });
    }

    @Override
    public <T extends Movie> void addMovie(T movie, Token token) {
        movieApi.createMovie("Bearer " + token.getAccessToken(), (ServerMovieDTO) movie)
                .enqueue(new Callback<ServerMovieDTO>() {
                    @Override
                    public void onResponse(Call<ServerMovieDTO> call, Response<ServerMovieDTO> response) {
                        if (!response.isSuccessful()) {
                            try {
                                if (response.errorBody() == null)
                                    Log.i("Ошибка создания фильма: ", String.valueOf(response.code()));
                                else
                                    Log.i("Ошибка создания фильма: ", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerMovieDTO> call, Throwable t) {

                    }
                });
    }

    @Override
    public User getUserById(String id) {
        return userDAO.getUserById(id);
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
        userApi.updateUserInfo("Bearer " + user.getToken().getAccessToken(),
                new HashMap<String, String>() {{
                    put("firstName", user.getFirstName());
                    put("lastName", user.getLastName());
                }}).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка обновления данных польхователя: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка обновления данных пользователя: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void addFavourite(FavoriteMovie favoriteMovie) {
        movieApi.addToFav(favoriteMovie.getMovieId(),
                "Bearer " +
                        favoriteMovie.getUser().getToken().getAccessToken()).enqueue(new Callback<ServerMovieDTO>() {
            @Override
            public void onResponse(Call<ServerMovieDTO> call, Response<ServerMovieDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServerMovieDTO movie = response.body();
                    if (movie.getCover() != null) {
                        if (movie.getCover().getContent() != null) {
                            Bitmap cover = CoverProvider.getFromServer(movie.getCover().getContent());
                            if (cover != null) {
                                String filename = movie.getCover().getFileName() != null
                                        ? movie.getCover().getFileName()
                                        : String.format("image_%s.png", Instant.now().toEpochMilli());
                                try {
                                    File imageFile = new File(imagesDir, filename);
                                    OutputStream stream = new FileOutputStream(imageFile);
                                    cover.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.flush();
                                    stream.close();
                                    movie.setCoverUri(Uri.fromFile(imageFile).toString());
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            }
                        }
                    }
                    if (!movieDAO.isMovieExists(movie.getId()))
                        movieDAO.addMovie(
                                (RoomMovieDTO) MovieDTOFactory.formStoreMovieDTO(movie));
                    movieDAO.addFavourite(favoriteMovie);
                } else if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка добавления в список избранных: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка добавления в список избранных: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerMovieDTO> call, Throwable t) {

            }
        });
    }

    @Override
    public void removeFavourite(FavoriteMovie favoriteMovie) {
        movieApi.removeFromFav(favoriteMovie.getMovieId(),
                "Bearer " +
                        favoriteMovie.getUser().getToken().getAccessToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    movieDAO.removeFavourite(favoriteMovie);
                } else {
                    try {
                        if (response.errorBody() == null)
                            Log.i("Ошибка удаления из списка избранных: ", String.valueOf(response.code()));
                        else
                            Log.i("Ошибка удаления из списка избранных: ", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public Boolean isFavorite(String userId, String movieId) {
        return movieDAO.isFavorite(userId, movieId);
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
        Token token = tokenDAO.getByUserId(userId);
        if (token != null) {
            tokenService.checkConnection("Bearer " +
                    token.getAccessToken()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        tokenService.refreshToken("Bearer "
                                        + token.getAccessToken(),
                                Collections.singletonMap("refreshToken", token.getRefreshToken()))
                                .enqueue(new Callback<Map>() {
                                    @Override
                                    public void onResponse(Call<Map> call, Response<Map> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            token.setAccessToken((String) response.body().get("token"));
                                            token.setRefreshToken((String) response.body().get("refreshToken"));
                                            tokenDAO.updateToken(token);
                                        } else if (!response.isSuccessful()) {
                                            try {
                                                if (response.errorBody() == null)
                                                    Log.i("Ошибка обновления токена: ", String.valueOf(response.code()));
                                                else
                                                    Log.i("Ошибка обновления токена: ", response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Map> call, Throwable t) {

                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
        return token;
    }
}