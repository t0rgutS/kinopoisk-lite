package com.kinopoisklite.repository.dtoFactory;

import com.kinopoisklite.model.Role;
import com.kinopoisklite.model.User;
import com.kinopoisklite.repository.dtoVersion;
import com.kinopoisklite.repository.room.model.RoomUserDTO;

public class UserDTOFactory {
    private static dtoVersion version;

    public static void init(dtoVersion version) {
        UserDTOFactory.version = version;
    }

    public static User formCreateUserDTO(String id, String login, String firstName, String lastName,
                                         String password, Boolean external, Role role) {
        if(version == dtoVersion.ROOM) {
            RoomUserDTO userDTO = new RoomUserDTO();
            userDTO.setId(id);
            userDTO.setLogin(login);
            userDTO.setFirstName(firstName);
            userDTO.setLastName(lastName);
            userDTO.setPassword(password);
            userDTO.setExternal(external);
            userDTO.setRoleId(role.getId());
            return userDTO;
        } else
            return null;
    }

    public static User formUpdateUserDTO(String id, String login, String firstName, String lastName,
                               String password, Boolean external, Role role, User initial) {
        if(version == dtoVersion.ROOM) {
            RoomUserDTO userDTO = new RoomUserDTO();
            userDTO.setId(id);
            userDTO.setLogin(login);
            userDTO.setFirstName(firstName != null ? !firstName.isEmpty()
                    ? firstName : initial.getFirstName()
                    : initial.getFirstName());
            userDTO.setLastName(lastName != null ? !lastName.isEmpty()
                    ? lastName : initial.getLastName()
                    : initial.getLastName());
            userDTO.setPassword(password);
            userDTO.setExternal(external);
            userDTO.setRoleId(role != null ? role.getId() : initial.getRole().getId());
            return userDTO;
        } else
            return null;
    }
}
