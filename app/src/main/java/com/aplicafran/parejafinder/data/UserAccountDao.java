package com.aplicafran.parejafinder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserAccountDao {
    @Insert
    void insert(UserAccount account);

    @Update
    void update(UserAccount account);

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    UserAccount findByEmail(String email);

    @Query("SELECT * FROM user_accounts ORDER BY isAdmin DESC, email ASC")
    List<UserAccount> getAll();

    @Query("DELETE FROM user_accounts WHERE email = :email")
    void deleteByEmail(String email);
}
