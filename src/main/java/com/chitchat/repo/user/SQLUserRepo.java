package com.chitchat.repo.user;
import com.chitchat.config.db.ConnectSQL;
import com.chitchat.config.security.DataCrypt;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLUserRepo implements UserRepo {
    private static final Logger logger = Logger.getLogger(SQLUserRepo.class.getName());

    ConnectSQL sql;

    public void getConnection() {
        sql = ConnectSQL.getInstance();
    }

    public boolean userExist(String uname) {
        String query = "select * from users where uname=?";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);
            ResultSet userset = statement.executeQuery();
            return userset.next();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to check user existence for: " + uname, e);
        }

        return false;
    }

    public boolean userLogin(String uname, String password) {
        String query = "select upass from users where uname=?";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);
            ResultSet userset = statement.executeQuery();

            if (userset.next()) {
                String storedHash = userset.getString(1);
                return DataCrypt.checkPassword(password, storedHash);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to authenticate user: " + uname, e);
        }

        return false;
    }

    public boolean addUser(String uname, String password) {
        String query = "insert into users(uname, upass) values(?,?)";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);
            statement.setString(2, DataCrypt.hashPassword(password));

            if (statement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to add user: " + uname, e);
        }

        return false;
    }

    public boolean checkKey(String uname, String sessionkey) {
        String query = "select sessionkey from users where uname=?";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);
            ResultSet keyset = statement.executeQuery();
            keyset.next();

            if (sessionkey.equals(keyset.getString(1))) {
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to check session key for user: " + uname, e);
        }

        return false;
    }

    public void setKey(String uname, String sessionkey) {
        String query = "update users set sessionkey=? where uname=?";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, sessionkey);
            statement.setString(2, uname);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to set session key for user: " + uname, e);
        }
    }
}
