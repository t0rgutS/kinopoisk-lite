package com.kinopoisklite.repository;

import android.app.Application;

import com.google.gson.Gson;
import com.kinopoisklite.repository.network.BackgroundInfoProvider;
import com.kinopoisklite.repository.network.ServerRepository;
import com.kinopoisklite.repository.php.RemoteRepository;
import com.kinopoisklite.repository.room.RoomRepository;
import com.kinopoisklite.security.AuthManager;
import com.kinopoisklite.security.local.LocalAuthManager;
import com.kinopoisklite.security.network.ServerAuthManager;
import com.kinopoisklite.security.SessionManager;
import com.kinopoisklite.security.SessionManagerImpl;

public class ResourceManager {
    private static Repository repository;
    private static Gson gson;
    private static BackgroundInfoProvider provider;
    private static SessionManager sessionManager;
    private static AuthManager authManager;

    public static void init(Application application, Version version) {
        if (version == Version.ROOM) {
            repository = new RoomRepository(application);
            authManager = new LocalAuthManager();
        } else if (version == Version.NETWORK) {
            repository = new ServerRepository(application);
            authManager = new ServerAuthManager();
        }
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

    public static BackgroundInfoProvider getProvider() {
        if (provider == null)
            provider = new BackgroundInfoProvider();
        return provider;
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    public static AuthManager getAuthManager() {
        return authManager;
    }
}