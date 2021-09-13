package com.kinopoisklite.repository.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kinopoisklite.model.dto.room.RoomMovieDTO;
import com.kinopoisklite.model.entity.AgeRating;
import com.kinopoisklite.repository.room.dao.AgeRatingDAO;
import com.kinopoisklite.repository.room.dao.MovieDAO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;

@Database(entities = {RoomMovieDTO.class, AgeRating.class}, version = 1, exportSchema = false)
public abstract class MovieRoomDatabase extends RoomDatabase {
    public abstract MovieDAO movieDAO();

    public abstract AgeRatingDAO ageRatingDAO();

    private static final List<AgeRating> initialRatings = List.of(
            new AgeRating(1L, "0+", 0),
            new AgeRating(2L, "6+", 6),
            new AgeRating(3L, "12+", 12),
            new AgeRating(4L, "16+", 16),
            new AgeRating(5L, "18+", 18)
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
