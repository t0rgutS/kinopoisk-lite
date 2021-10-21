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
@Entity(tableName = "roles")
public class Role {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private Long id;

    @ColumnInfo(name = "role_name")
    @NotNull
    private String roleName;

    @ColumnInfo(name = "access_level")
    @NotNull
    private Integer accessLevel;

    @Ignore
    public Role(Long id, String roleName, Integer accessLevel) {
        this.id = id;
        this.roleName = roleName;
        this.accessLevel = accessLevel;
    }
}