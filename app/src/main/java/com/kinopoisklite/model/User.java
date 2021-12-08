package com.kinopoisklite.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @ColumnInfo
    @NonNull
    protected String id;

    @ColumnInfo
    @NotNull
    protected String login;

    @ColumnInfo
    protected String password;

    @ColumnInfo(name = "first_name")
    protected String firstName;

    @ColumnInfo(name = "last_name")
    protected String lastName;

    @ColumnInfo
    @NotNull
    protected Boolean external;

    @ColumnInfo
    @NotNull
    protected Roles role;

    @Ignore
    protected Token token;

    @Ignore
    protected LiveData<List<Movie>> favoriteMovies;

    public enum Roles {
        ROLE_USER, ROLE_MODER, ROLE_ADMIN
    }

    @Ignore
    public User(String id, String login, String firstName,
                String lastName, Boolean external, Roles role) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.external = external;
        this.role = role;
    }
}