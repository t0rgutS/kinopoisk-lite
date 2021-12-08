package com.kinopoisklite.security.local;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.ResourceManager;
import com.kinopoisklite.repository.dtoFactory.UserRequestFactory;
import com.kinopoisklite.repository.network.userInfo.GoogleUserInfoProvider;
import com.kinopoisklite.repository.network.userInfo.UserInfoProvider;
import com.kinopoisklite.security.AuthManager;
import com.kinopoisklite.security.AuthenticationProviders;
import com.kinopoisklite.security.TokenProvider;

import java.io.IOException;

public class LocalAuthManager implements AuthManager {
    @Override
    public LiveData<User> authenticateInternal(String login, String password) throws IOException {
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

    @Override
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
                        User newUser = new User(
                                (String) userInfo.get("id"),
                                (String) userInfo.get("id"),
                                (String) userInfo.get("given_name"),
                                (String) userInfo.get("family_name"),
                                true,
                                User.Roles.ROLE_USER
                        );
                        ResourceManager.getRepository().addUser(newUser);
                        ResourceManager.getSessionManager().setSessionUser(newUser);
                        //  userLiveData.setValue(newUser);
                        return newUser;
                    } else {
                        User updatedUser = UserRequestFactory.formUpdateUserRequest(
                                existingUser.getId(),
                                existingUser.getLogin(),
                                (String) userInfo.get("given_name"),
                                (String) userInfo.get("family_name"),
                                null,
                                true,
                                existingUser.getRole(),
                                existingUser
                        );
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
}
