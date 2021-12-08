package com.kinopoisklite.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "tokens")
public class Token {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private Long id;

    @ColumnInfo(name = "access_token")
    @NotNull
    private String accessToken;

    @ColumnInfo(name = "refresh_token")
    @NotNull
    private String refreshToken;

    @ColumnInfo(name = "user_id")
    @NotNull
    private String userId;
}
