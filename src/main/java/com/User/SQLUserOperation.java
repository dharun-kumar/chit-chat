package com.User;

import com.Database.ConnectSQL;
import com.Others.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserOperation implements UserOperation
{
    ConnectSQL sql;

    public void getConnection()
    {
        sql = ConnectSQL.getInstance();
    }

    public boolean userExist(String uname)
    {
        String query = "select * from users where uname=?";

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);

            ResultSet userset = statement.executeQuery();
            return userset.next();
        }

        catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean userLogin(String uname, String password) {
        String query = "select * from users where uname=? and upass=?";
        password = DataCrypt.encode(password);

        try {
            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);
            statement.setString(2, password);
            ResultSet userset = statement.executeQuery();

            return userset.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addUser(String uname, String password)
    {
        password = DataCrypt.encode(password);

        String query = "insert into users(uname, upass) values(?,?)";

        try {

            PreparedStatement statement = sql.con.prepareStatement(query);

            statement.setString(1, uname);
            statement.setString(2, password);

            if(statement.executeUpdate()==1)
                return true;
        }

        catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkKey(String uname, String sessionkey)
    {
        String query = "select sessionkey from users where uname=?";

        try {

            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, uname);

            ResultSet keyset = statement.executeQuery();
            keyset.next();

            if(sessionkey.equals(keyset.getString(1)))
                return true;
        }

        catch(Exception e) {
        }

        return false;
    }

    public void setKey(String uname, String sessionkey)
    {
        String query = "update users set sessionkey=? where uname=?";

        try {

            PreparedStatement statement = sql.con.prepareStatement(query);
            statement.setString(1, sessionkey);
            statement.setString(2, uname);

            int count = statement.executeUpdate();
        }

        catch(Exception e) {
        }
    }
}
