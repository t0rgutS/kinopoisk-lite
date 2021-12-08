package com.kinopoisklite.repository.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kinopoisklite.model.Token;

@Dao
public interface TokenDAO {
    @Query("SELECT * FROM tokens WHERE user_id=:userId")
    Token getByUserId(String userId);

    @Insert
    void addToken(Token token);

    @Update
    void updateToken(Token token);

    @Delete
    void deleteToken(Token token);
}
