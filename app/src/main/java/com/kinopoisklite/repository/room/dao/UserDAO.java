package com.kinopoisklite.repository.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kinopoisklite.model.User;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM users WHERE id=:id")
    User getUserById(String id);

    @Query("SELECT * FROM users WHERE login=:login")
    LiveData<User> getUserByLogin(String login);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE id = :id)")
    Boolean isUserExists(String id);

    @Insert
    void addUser(User user);

    @Update
    void updateUser(User user);
}
