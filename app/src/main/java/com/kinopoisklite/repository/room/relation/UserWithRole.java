package com.kinopoisklite.repository.room.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kinopoisklite.model.Role;
import com.kinopoisklite.repository.room.model.RoomUserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserWithRole {
    @Embedded
    private RoomUserDTO roomUserDTO;

    @Relation(parentColumn = "role_id", entityColumn = "id", entity = Role.class)
    private Role role;
}
