package com.aplicafran.parejafinder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CandidateProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CandidateProfile profile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CandidateProfile> profiles);

    @Query("SELECT * FROM candidate_profiles ORDER BY nombre ASC")
    List<CandidateProfile> getAll();

    @Query("SELECT * FROM candidate_profiles WHERE email = :email LIMIT 1")
    CandidateProfile findByEmail(String email);

    @Update
    void update(CandidateProfile profile);

    @Query("DELETE FROM candidate_profiles WHERE email = :email")
    void deleteByEmail(String email);

    @Query("SELECT COUNT(*) FROM candidate_profiles")
    int count();
}
