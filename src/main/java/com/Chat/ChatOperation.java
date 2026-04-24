package com.Chat;

import java.util.ArrayList;

public interface ChatOperation
{
    void getConnection();

    ArrayList<String> getAllUsers(String user);

    void sendMessage(String sender, String receiver, String message);

    ArrayList<String[]> getChats(String sender, String receiver, String lasttime);
}
