package com.chitchat.config.security;
import org.mindrot.jbcrypt.BCrypt;

public class DataCrypt {
    private static final int BCRYPT_COST = 10;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_COST));
    }

    public static boolean checkPassword(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }
}
