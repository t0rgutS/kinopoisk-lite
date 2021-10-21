package com.kinopoisklite.repository.room.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.kinopoisklite.model.User;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "users")
public class RoomUserDTO extends User {
    @ColumnInfo(name = "role_id")
    @NotNull
    private Long roleId;

    @Ignore
    public RoomUserDTO(String id, String login, String firstName, String lastName,
                       String password, Boolean external, @NotNull Long roleId) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.external = external;
        this.roleId = roleId;
    }
}
