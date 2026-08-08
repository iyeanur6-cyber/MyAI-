package com.example.banglalocalai.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    void insertMessage(ChatEntity chat);

    @Query("SELECT * FROM chat_history ORDER BY timestamp ASC")
    List<ChatEntity> getAllMessages();

    @Query("DELETE FROM chat_history")
    void clearHistory();
}
