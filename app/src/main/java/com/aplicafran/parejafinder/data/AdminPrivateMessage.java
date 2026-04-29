package com.aplicafran.parejafinder.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admin_private_messages")
public class AdminPrivateMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String targetEmail;
    public String body;
    public long createdAt;

    public AdminPrivateMessage(String targetEmail, String body, long createdAt) {
        this.targetEmail = targetEmail;
        this.body = body;
        this.createdAt = createdAt;
    }
}
