package com.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectSQL
{
    public Connection con;

    private ConnectSQL()
    {
        String url = "jdbc:postgresql://db:5432/mydb";
        String uname = "admin";
        String upass = "admin";

        try {
            con = DriverManager.getConnection(url, uname, upass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ConnectSQL sql;

    public static ConnectSQL getInstance()
    {
        if (sql == null)
        {
            synchronized (ConnectSQL.class)
            {
                if (sql==null)
                    sql = new ConnectSQL();
            }
        }
        return sql;
    }
}