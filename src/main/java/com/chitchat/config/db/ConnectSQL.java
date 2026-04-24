package com.chitchat.config.db;
import com.chitchat.config.AppConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectSQL {
    private static final Logger logger = Logger.getLogger(ConnectSQL.class.getName());

    public Connection con;

    private ConnectSQL() {
        try {
            // Force the JVM to load the PostgreSQL driver class
            Class.forName("org.postgresql.Driver");

            String url = AppConfig.get("db.url");
            String uname = AppConfig.get("db.username");
            String upass = AppConfig.get("db.password");
            con = DriverManager.getConnection(url, uname, upass);
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to connect to PostgreSQL", e);
            throw new RuntimeException("Failed to connect to PostgreSQL", e);
        }
    }

    private static ConnectSQL sql;

    public static ConnectSQL getInstance() {
        if (sql == null) {
            synchronized (ConnectSQL.class) {
                if (sql == null) {
                    sql = new ConnectSQL();
                }
            }
        }
        return sql;
    }
}
