package com.chitchat.repo.chat;
import com.chitchat.model.Chat;
import java.time.Instant;
import java.util.ArrayList;

public interface ChatRepo {
    void getConnection();
    ArrayList<String> getAllUsers(String user);
    void sendMessage(String sender, String receiver, String message);
    ArrayList<Chat> getChats(String sender, String receiver, Instant lasttime);
}
