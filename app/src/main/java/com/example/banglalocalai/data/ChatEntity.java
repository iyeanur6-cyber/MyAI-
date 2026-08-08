package com.example.banglalocalai.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_history")
public class ChatEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String sender;
    public String message;
    public long timestamp;

    public ChatEntity(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }
}
