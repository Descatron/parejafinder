package com.aplicafran.parejafinder.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserMatchDao {
    @Insert
    void insert(UserMatch match);

    @Query("SELECT COUNT(*) FROM user_matches WHERE username = :username AND candidateId = :candidateId")
    int exists(String username, int candidateId);

    @Query("SELECT matchType FROM user_matches WHERE username = :username AND candidateId = :candidateId LIMIT 1")
    String getType(String username, int candidateId);

    @Query("UPDATE user_matches SET matchType = :matchType WHERE username = :username AND candidateId = :candidateId")
    void updateType(String username, int candidateId, String matchType);

    @Query("SELECT c.id AS candidateId, c.nombre, c.edad, c.ciudad, c.intereses, m.matchType, " +
            "IFNULL((SELECT COUNT(*) FROM chat_messages cm " +
            "WHERE cm.username = m.username AND cm.candidateId = m.candidateId AND cm.sender = 'MATCH' AND cm.isRead = 0), 0) AS unreadCount " +
            "FROM candidate_profiles c INNER JOIN user_matches m ON c.id = m.candidateId " +
            "WHERE m.username = :username ORDER BY m.id DESC")
    List<MatchWithProfile> getMatchesForUser(String username);
}
