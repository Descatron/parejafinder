package com.aplicafran.parejafinder.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                CandidateProfile.class,
                UserMatch.class,
                UserAccount.class,
                ChatMessage.class,
                AdminPrivateMessage.class,
                AdminActionLog.class
        },
        version = 10,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract CandidateProfileDao candidateProfileDao();
    public abstract UserMatchDao userMatchDao();
    public abstract UserAccountDao userAccountDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract AdminPrivateMessageDao adminPrivateMessageDao();
    public abstract AdminActionLogDao adminActionLogDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "pareja_finder_db"
                            )
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
