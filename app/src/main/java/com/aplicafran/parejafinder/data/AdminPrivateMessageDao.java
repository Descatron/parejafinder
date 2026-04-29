package com.aplicafran.parejafinder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdminPrivateMessageDao {
    @Insert
    void insert(AdminPrivateMessage message);

    @Query("SELECT * FROM admin_private_messages WHERE targetEmail = :targetEmail ORDER BY createdAt DESC")
    List<AdminPrivateMessage> getForUser(String targetEmail);
}
