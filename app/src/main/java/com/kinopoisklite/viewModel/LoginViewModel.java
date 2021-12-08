package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.BuildConfig;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.security.AuthenticationProviders;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {


    public LiveData<User> authenticate(String login, String password) throws IOException {
        return ResourceManager.getAuthManager().authenticateInternal(login, password);
    }

    public LiveData<User> authenticateExternal(String authCode, AuthenticationProviders authProvider) throws IOException {
        return ResourceManager.getAuthManager().authenticateExternal(authCode, authProvider);
    }

    public Map<String, String> getProviderParams(AuthenticationProviders authProvider) {
        Map<String, String> providerParams = new HashMap<>();
        switch (authProvider) {
            case VK: {
                //TODO not implemented yet
            }
            case GOOGLE: {
                String uri = "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "client_id=" + BuildConfig.GOOGLE_CLIENT_ID + "&" +
                        "redirect_uri=" + BuildConfig.GOOGLE_REDIRECT_URI + "&" +
                        "scope=" + BuildConfig.GOOGLE_SCOPE + "&" +
                        "access_type=offline&" +
                        "response_type=code";
                providerParams.put("uri", uri);
                providerParams.put("authCodeParam", "code");
                providerParams.put("errorParam", "error");
            }
        }
        return providerParams;
    }
}