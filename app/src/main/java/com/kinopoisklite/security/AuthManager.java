package com.kinopoisklite.security;

import androidx.lifecycle.LiveData;

import com.kinopoisklite.model.User;

import java.io.IOException;

public interface AuthManager {
    LiveData<User> authenticateInternal(String login, String password) throws IOException;

    LiveData<User> authenticateExternal(String authCode,
                                        AuthenticationProviders authProvider) throws IOException;
}
