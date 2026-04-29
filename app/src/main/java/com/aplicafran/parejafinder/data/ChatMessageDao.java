package com.aplicafran.parejafinder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDao {
    @Insert
    void insert(ChatMessage message);

    @Query("SELECT * FROM chat_messages WHERE username = :username AND candidateId = :candidateId ORDER BY createdAt ASC, id ASC")
    List<ChatMessage> getConversation(String username, int candidateId);

    @Query("UPDATE chat_messages SET isRead = 1 WHERE username = :username AND candidateId = :candidateId AND sender = 'MATCH'")
    void markConversationAsRead(String username, int candidateId);
}
