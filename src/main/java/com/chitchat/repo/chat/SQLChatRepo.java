package com.chitchat.repo.chat;
import com.chitchat.config.db.*;
import com.chitchat.model.Chat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLChatRepo implements ChatRepo {
    private static final Logger logger = Logger.getLogger(SQLChatRepo.class.getName());

    ConnectSQL sql;

    public void getConnection() {
        sql = ConnectSQL.getInstance();
    }

    public ArrayList<String> getAllUsers(String user) {
        ArrayList<String> users = new ArrayList<>();
        String query = "select uname from users where uname != ?";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, user);
            ResultSet userset = statement.executeQuery();

            while (userset.next()) {
                users.add(userset.getString(1));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to fetch users from SQL", e);
        }

        return users;
    }

    public void sendMessage(String sender, String receiver, String message) {
        String query = "insert into chats (sender, receiver, message) values (?, ?, ?)";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, sender);
            statement.setString(2, receiver);
            statement.setString(3, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to send message to SQL", e);
        }
    }

    public ArrayList<Chat> getChats(String sender, String receiver, Instant lasttime) {
        ArrayList<Chat> chats = new ArrayList<>();
        String query = "select * from chats where (delivertime > ?) and ((sender=? and receiver=?) or (sender=? and receiver=?)) order by delivertime asc";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setTimestamp(1, Timestamp.from(lasttime));
            statement.setString(2, sender);
            statement.setString(3, receiver);
            statement.setString(4, receiver);
            statement.setString(5, sender);
            ResultSet messageset = statement.executeQuery();

            while (messageset.next()) {
                String msgSender = messageset.getString(2);
                String msgReceiver = messageset.getString(3);
                String msgText = messageset.getString(4);
                Instant delivertime = messageset.getTimestamp(5).toInstant();
                chats.add(new Chat(msgSender, msgReceiver, msgText, delivertime));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to fetch chats from SQL", e);
        }

        return chats;
    }
}
