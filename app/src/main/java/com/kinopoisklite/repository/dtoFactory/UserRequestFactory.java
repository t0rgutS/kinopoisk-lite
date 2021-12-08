package com.kinopoisklite.repository.dtoFactory;

import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.Version;

public class UserRequestFactory {
    private static Version version;

    public static void init(Version version) {
        UserRequestFactory.version = version;
    }

    public static User formUpdateUserRequest(String id, String login, String firstName, String lastName,
                                             String password, Boolean external, User.Roles role, User initial) {
        if (version == Version.ROOM) {
            User user = new User();
            user.setId(id);
            user.setLogin(login);
            user.setFirstName(firstName != null ? !firstName.isEmpty()
                    ? firstName : initial.getFirstName()
                    : initial.getFirstName());
            user.setLastName(lastName != null ? !lastName.isEmpty()
                    ? lastName : initial.getLastName()
                    : initial.getLastName());
            user.setPassword(password);
            user.setExternal(external);
            user.setRole(role != null ? role : initial.getRole());
            return user;
        } else
            return null;
    }
}
