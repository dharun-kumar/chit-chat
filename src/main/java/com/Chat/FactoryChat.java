package com.Chat;

public class FactoryChat
{
    public static ChatOperation getInstance(String database)
    {
        if(database.equals("ES"))
            return new ESChatOperation();

        else if(database.equals("SQL"))
            return new SQLChatOperation();

        else
            return null;
    }
}

