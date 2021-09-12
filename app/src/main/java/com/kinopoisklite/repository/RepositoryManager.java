package com.kinopoisklite.repository;

import com.kinopoisklite.repository.remote.RemoteRepository;

public class RepositoryManager {
    private static Repository repository;

    public static Repository getRepository() {
        if(repository == null)
            repository = new RemoteRepository();
        return repository;
    }
}