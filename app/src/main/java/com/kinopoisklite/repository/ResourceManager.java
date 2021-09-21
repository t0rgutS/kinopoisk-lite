package com.kinopoisklite.repository;

import android.app.Application;

import com.google.gson.Gson;
import com.kinopoisklite.repository.remote.RemoteRepository;
import com.kinopoisklite.repository.room.RoomRepository;

public class ResourceManager {
    private static Repository repository;
    private static Gson gson;

    public static void initRoom(Application application) {
        repository = new RoomRepository(application);
    }

    public static Gson getGson() {
        if(gson == null)
            gson = new Gson();
        return gson;
    }

    public static Repository getRepository() {
        if (repository == null)
            repository = new RemoteRepository();
        return repository;
    }
}