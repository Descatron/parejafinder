package com.aplicafran.parejafinder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdminActionLogDao {
    @Insert
    void insert(AdminActionLog log);

    @Query("SELECT * FROM admin_action_logs ORDER BY createdAt DESC, id DESC")
    List<AdminActionLog> getAll();
}
