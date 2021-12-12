package com.kinopoisklite.security.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kinopoisklite.model.Token;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.dtoFactory.UserRequestFactory;
import com.kinopoisklite.repository.network.userInfo.ServerUserInfoProvider;
import com.kinopoisklite.repository.network.userInfo.UserInfoProvider;
import com.kinopoisklite.security.AuthManager;
import com.kinopoisklite.security.AuthenticationProviders;
import com.kinopoisklite.security.TokenProvider;

import java.io.IOException;

public class ServerAuthManager implements AuthManager {

    private LiveData<User> authenticate(TokenProvider tokenProvider, String credentials, boolean external) throws IOException {
        return Transformations.switchMap(tokenProvider.getToken(credentials), providerResponse -> {
            Token tokenData = new Token();
            tokenData.setAccessToken((String) providerResponse.get("token"));
            tokenData.setRefreshToken((String) providerResponse.get("refreshToken"));
            UserInfoProvider userInfoProvider = new ServerUserInfoProvider();
            try {
                return Transformations.map(userInfoProvider.getUserInfo(tokenData.getAccessToken()),
                        userInfo -> {
                            User existingUser = ResourceManager.getRepository()
                                    .getUserById((String) userInfo.get("id"));
                            if (existingUser == null) {
                                User newUser = new User(
                                        (String) userInfo.get("id"),
                                        (String) userInfo.get("login"),
                                        (String) userInfo.get("firstName"),
                                        (String) userInfo.get("lastName"),
                                        external,
                                        User.Roles.valueOf((String) userInfo.get("role"))
                                );
                                tokenData.setUserId(newUser.getId());
                                newUser.setToken(tokenData);
                                newUser.setFavoriteMovies(ResourceManager
                                        .getRepository().getUserFavourites(newUser));
                                ResourceManager.getRepository().addToken(tokenData);
                                ResourceManager.getRepository().addUser(newUser);
                                ResourceManager.getSessionManager().setSessionUser(newUser);
                                return newUser;
                            } else {
                                User updatedUser = UserRequestFactory.formUpdateUserRequest(
                                        existingUser.getId(),
                                        existingUser.getLogin(),
                                        (String) userInfo.get("firstName"),
                                        (String) userInfo.get("lastName"),
                                        null,
                                        external,
                                        existingUser.getRole(),
                                        existingUser
                                );
                                tokenData.setUserId(updatedUser.getId());
                                updatedUser.setToken(tokenData);
                                updatedUser.setFavoriteMovies(ResourceManager
                                        .getRepository().getUserFavourites(updatedUser));
                                ResourceManager.getRepository().addToken(tokenData);
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

    @Override
    public LiveData<User> authenticateInternal(String login, String password) throws IOException {
        TokenProvider tokenProvider = new BasicTokenProvider();
        return authenticate(tokenProvider, login + ":" + password, false);
    }

    @Override
    public LiveData<User> authenticateExternal(String authCode, AuthenticationProviders authProvider) throws IOException {
        TokenProvider tokenProvider = new GoogleTokenProvider();
        return authenticate(tokenProvider, authCode, true);
    }
}
