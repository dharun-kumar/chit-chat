package com.chitchat.repo.chat;
import com.chitchat.config.AppConfig;

public class FactoryChat {
    public static ChatRepo getInstance() {
        String dbType = AppConfig.get("db.type");
        if (dbType.equals("ES")) {
            return new ESChatRepo();
        }
        if (dbType.equals("SQL")) {
            return new SQLChatRepo();
        }
        throw new UnsupportedOperationException("Unsupported database type: " + dbType);
    }
}
