package com.kinopoisklite.repository.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kinopoisklite.model.AgeRating;
import com.kinopoisklite.model.FavoriteMovie;
import com.kinopoisklite.model.Role;
import com.kinopoisklite.repository.room.dao.AgeRatingDAO;
import com.kinopoisklite.repository.room.dao.MovieDAO;
import com.kinopoisklite.repository.room.dao.RoleDAO;
import com.kinopoisklite.repository.room.dao.UserDAO;
import com.kinopoisklite.repository.room.model.RoomMovieDTO;
import com.kinopoisklite.repository.room.model.RoomUserDTO;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

@Database(entities = {RoomMovieDTO.class, AgeRating.class,
        RoomUserDTO.class, FavoriteMovie.class, Role.class}, version = 2, exportSchema = false)
public abstract class MovieRoomDatabase extends RoomDatabase {
    public abstract MovieDAO movieDAO();

    public abstract AgeRatingDAO ageRatingDAO();

    public abstract RoleDAO roleDAO();

    public abstract UserDAO userDAO();

    private static final List<AgeRating> initialRatings = List.of(
            new AgeRating(1L, "0+", 0),
            new AgeRating(2L, "6+", 6),
            new AgeRating(3L, "12+", 12),
            new AgeRating(4L, "16+", 16),
            new AgeRating(5L, "18+", 18)
    );

    private static final List<Role> initialRoles = List.of(
            new Role(1L, "Пользователь", 1),
            new Role(2L, "Модератор", 2),
            new Role(3L, "Администратор", 3)
    );

    private static final List<RoomUserDTO> initialUsers = List.of(
            new RoomUserDTO("e6d8a02f-e52b-4728-ac86-4e7c4ae558ea", "test", "Тестер", "Тестовый",
                    "test", false, 1L),
            new RoomUserDTO("4e5b91cf-415a-44e4-8773-2246df950594", "moder", "Злой", "Модератор",
                    "moder", false, 2L),
            new RoomUserDTO("17ef1e6c-aa12-42a4-a2d6-f76db7c6f485", "admin", "Очень Злой", "Админ",
                    "admin", false, 3L)
    );

    private static volatile MovieRoomDatabase INSTANCE;

    private static final int THREAD_COUNT = 5;

    @Getter
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    public static MovieRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null)
            synchronized (MovieRoomDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MovieRoomDatabase.class, "KinopoiskLiteBase")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    executorService.execute(() -> {
                                        initialRatings.forEach(initialRating -> {
                                            if (!INSTANCE.ageRatingDAO().isRatingExists(initialRating.getId()))
                                                INSTANCE.ageRatingDAO().addAgeRating(initialRating);
                                        });
                                        initialRoles.forEach(initialRole -> {
                                            if (!INSTANCE.roleDAO().isRoleExists(initialRole.getId()))
                                                INSTANCE.roleDAO().addRole(initialRole);
                                        });
                                        initialUsers.forEach(initialUser -> {
                                            if (!INSTANCE.userDAO().isUserExists(initialUser.getId()))
                                                INSTANCE.userDAO().addUser(initialUser);
                                        });
                                    });
                                }
                            })
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
            }
        return INSTANCE;
    }
}
