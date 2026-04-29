package com.aplicafran.parejafinder.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "admin_action_logs")
public class AdminActionLog {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String adminEmail;
    public String actionType;
    public String targetEmail;
    public String details;
    public long createdAt;

    public AdminActionLog(String adminEmail, String actionType, String targetEmail, String details, long createdAt) {
        this.adminEmail = adminEmail;
        this.actionType = actionType;
        this.targetEmail = targetEmail;
        this.details = details;
        this.createdAt = createdAt;
    }
}
