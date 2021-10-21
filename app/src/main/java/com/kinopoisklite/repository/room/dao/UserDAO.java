package com.kinopoisklite.repository.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.kinopoisklite.repository.room.model.RoomUserDTO;
import com.kinopoisklite.repository.room.relation.UserWithRole;

@Dao
public interface UserDAO {
    @Transaction
    @Query("SELECT * FROM users WHERE id=:id")
    UserWithRole getUserById(String id);

    @Transaction
    @Query("SELECT * FROM users WHERE login=:login")
    LiveData<UserWithRole> getUserByLogin(String login);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE id = :id)")
    Boolean isUserExists(String id);

    @Insert
    void addUser(RoomUserDTO user);

    @Update
    void updateUser(RoomUserDTO user);
}
