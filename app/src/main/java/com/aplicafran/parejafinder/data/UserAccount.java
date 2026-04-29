package com.aplicafran.parejafinder.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_accounts")
public class UserAccount {
    @PrimaryKey
    @NonNull
    public String email;
    public String password;
    public int isAdmin;
    public String displayName;
    public int age;
    public String city;
    public String bio;
    public String photoUri;
    public int isBlocked;
    public int isOnline;
    public long lastSeenAt;

    public UserAccount(
            @NonNull String email,
            String password,
            int isAdmin,
            String displayName,
            int age,
            String city,
            String bio,
            String photoUri,
            int isBlocked,
            int isOnline,
            long lastSeenAt
    ) {
        this.email = email == null ? "" : email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.displayName = displayName == null ? "" : displayName;
        this.age = age;
        this.city = city == null ? "" : city;
        this.bio = bio == null ? "" : bio;
        this.photoUri = photoUri == null ? "" : photoUri;
        this.isBlocked = isBlocked;
        this.isOnline = isOnline;
        this.lastSeenAt = lastSeenAt;
    }
}
