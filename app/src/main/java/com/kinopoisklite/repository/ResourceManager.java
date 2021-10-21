package com.kinopoisklite.repository;

import android.app.Application;

import com.google.gson.Gson;
import com.kinopoisklite.repository.network.ExternalDataProvider;
import com.kinopoisklite.repository.remote.RemoteRepository;
import com.kinopoisklite.repository.room.RoomRepository;
import com.kinopoisklite.security.SessionManager;
import com.kinopoisklite.security.SessionManagerImpl;

public class ResourceManager {
    private static Repository repository;
    private static Gson gson;
    private static ExternalDataProvider provider;
    private static SessionManager sessionManager;

    public static void initRoom(Application application) {
        repository = new RoomRepository(application);
        sessionManager = new SessionManagerImpl(application.getApplicationContext());
    }

    public static Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    public static Repository getRepository() {
        if (repository == null)
            repository = new RemoteRepository();
        return repository;
    }

    public static ExternalDataProvider getProvider() {
        if (provider == null)
            provider = new ExternalDataProvider();
        return provider;
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }
}