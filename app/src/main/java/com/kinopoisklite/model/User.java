package com.kinopoisklite.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

    @Ignore
    protected Role role;
}