package com.aplicafran.parejafinder.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public int candidateId;
    public String sender; // "ME" o "MATCH"
    public String body;
    public long createdAt;
    public int isRead; // 0 no leido, 1 leido

    public ChatMessage(String username, int candidateId, String sender, String body, long createdAt, int isRead) {
        this.username = username;
        this.candidateId = candidateId;
        this.sender = sender;
        this.body = body;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }
}
