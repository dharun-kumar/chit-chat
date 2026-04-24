package com.chitchat.config.security;
import com.chitchat.repo.user.FactoryUser;
import com.chitchat.repo.user.UserRepo;
import java.util.UUID;

public class SessionKey {
    public static String generateKey() {
        return UUID.randomUUID().toString();
    }

    public static boolean checkSession(String uname, String loginkey) {
        UserRepo operation = FactoryUser.getInstance();
        operation.getConnection();
        return operation.checkKey(uname, loginkey);
    }
}
