package com.kinopoisklite.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kinopoisklite.model.Role;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;

import com.kinopoisklite.BuildConfig;
import com.kinopoisklite.repository.dtoFactory.UserDTOFactory;
import com.kinopoisklite.repository.network.GoogleUserInfoProvider;
import com.kinopoisklite.repository.network.UserInfoProvider;
import com.kinopoisklite.security.TokenProvider;
import com.kinopoisklite.security.google.GoogleTokenProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {
    public enum AuthenticationProviders {VK, GOOGLE}

    public LiveData<User> authenticate(String login, String password) {
        return Transformations.map(ResourceManager.getRepository().getUserByLogin(login), user -> {
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    ResourceManager.getSessionManager().setSessionUser(user);
                    return user;
                }
            }
            return null;
        });
    }

    public LiveData<User> authenticateExternal(String authCode, AuthenticationProviders authProvider) throws IOException {
        TokenProvider tokenProvider = new GoogleTokenProvider();
        return Transformations.switchMap(tokenProvider.getToken(authCode), providerResponse -> {
            String token = (String) providerResponse.get("access_token");
            UserInfoProvider userInfoProvider = new GoogleUserInfoProvider();
            try {
                return Transformations.map(userInfoProvider.getUserInfo(token), userInfo -> {
                    User existingUser = ResourceManager.getRepository()
                            .getUserById((String) userInfo.get("id"));
                    if (existingUser == null) {
                        Role defaultRole = ResourceManager.getRepository().getRoleById(1L);
                        User newUser = UserDTOFactory.formCreateUserDTO(
                                (String) userInfo.get("id"),
                                (String) userInfo.get("id"),
                                (String) userInfo.get("given_name"),
                                (String) userInfo.get("family_name"),
                                null,
                                true,
                                defaultRole
                        );
                        newUser.setRole(defaultRole);
                        ResourceManager.getRepository().addUser(newUser);
                        ResourceManager.getSessionManager().setSessionUser(newUser);
                        //  userLiveData.setValue(newUser);
                        return newUser;
                    } else {
                        User updatedUser = UserDTOFactory.formUpdateUserDTO(
                                existingUser.getId(),
                                existingUser.getLogin(),
                                (String) userInfo.get("given_name"),
                                (String) userInfo.get("family_name"),
                                null,
                                true,
                                existingUser.getRole(),
                                existingUser
                        );
                        updatedUser.setRole(existingUser.getRole());
                        ResourceManager.getRepository().updateUser(updatedUser);
                        ResourceManager.getSessionManager().setSessionUser(updatedUser);
                        return updatedUser;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new MutableLiveData<>();
        });
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