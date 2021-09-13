package com.kinopoisklite.repository;

import android.app.Application;

import com.kinopoisklite.repository.remote.RemoteRepository;
import com.kinopoisklite.repository.room.RoomRepository;

public class RepositoryManager {
    private static Repository repository;

    public static void initRoom(Application application) {
        repository = new RoomRepository(application);
    }

    public static Repository getRepository() {
        if (repository == null)
            repository = new RemoteRepository();
        return repository;
    }
}