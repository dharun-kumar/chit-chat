package com.Chat;

import com.Database.*;
import com.Others.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLChatOperation implements ChatOperation
{
    ConnectSQL sql;

    public void getConnection()
    {
        sql = ConnectSQL.getInstance();
    }

    public ArrayList<String> getAllUsers(String user)
    {
        ArrayList<String> users = new ArrayList<>();

        String query = "select uname from users where uname != ?";
        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, user);
            ResultSet userset = statement.executeQuery();

            while(userset.next())
                users.add(userset.getString(1));
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void sendMessage(String sender, String receiver, String message)
    {
        String query = "insert into chats (sender, receiver, message) values (?, ?, ?)";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);

            statement.setString(1, sender);
            statement.setString(2, receiver);
            statement.setString(3, DataCrypt.encode(message));

            int count = statement.executeUpdate();
        }

        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getChats(String sender, String receiver, String lasttime)
    {
        ArrayList<String[]> chats = new ArrayList<>();

        String query = "select * from chats where (delivertime>?) and ((sender=? and receiver=?) or (sender=? and receiver=?)) order by delivertime asc";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, lasttime);
            statement.setString(2, sender);
            statement.setString(3, receiver);

            statement.setString(4, receiver);
            statement.setString(5, sender);
            ResultSet messageset = statement.executeQuery();

            while (messageset.next()) {
                String[] chat = new String[4];

                chat[0] = messageset.getString(3);
                chat[1] = messageset.getString(2);
                chat[2] = DataCrypt.decode(messageset.getString(4));
                chat[3] = messageset.getString(5);

                chats.add(chat);
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        return chats;
    }
}
