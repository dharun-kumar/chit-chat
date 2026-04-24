package com.chitchat.repo.user;
import com.chitchat.config.AppConfig;

public class FactoryUser {
    public static UserRepo getInstance() {
        String dbType = AppConfig.get("db.type");
        if (dbType.equals("ES")) {
            return new ESUserRepo();
        }
        if (dbType.equals("SQL")) {
            return new SQLUserRepo();
        }
        throw new UnsupportedOperationException("Unsupported database type: " + dbType);
    }
}
