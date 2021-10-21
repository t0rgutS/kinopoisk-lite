package com.kinopoisklite.repository.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.kinopoisklite.model.Role;

@Dao
public interface RoleDAO {
    @Query("SELECT * FROM roles WHERE id=:id")
    Role getRoleById(Long id);

    @Query("SELECT EXISTS(SELECT * FROM roles WHERE id = :id)")
    Boolean isRoleExists(Long id);

    @Insert
    void addRole(Role role);
}
