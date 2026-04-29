package com.aplicafran.parejafinder.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_matches")
public class UserMatch {
    public static final String TYPE_LIKE = "LIKE";
    public static final String TYPE_SUPER_LIKE = "SUPER_LIKE";

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public int candidateId;
    public String matchType;

    public UserMatch(String username, int candidateId, String matchType) {
        this.username = username;
        this.candidateId = candidateId;
        this.matchType = matchType;
    }
}
