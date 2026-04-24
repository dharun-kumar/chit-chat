package com.Others;

import com.User.FactoryUser;
import com.User.UserOperation;

import java.util.UUID;

public class SessionKey
{
    public static String generateKey()
    {
        return UUID.randomUUID().toString();
    }

    public static boolean checkSession(String uname, String loginkey)
    {
        UserOperation operation = FactoryUser.getInstance("SQL");
        operation.getConnection();

        return operation.checkKey(uname,loginkey);
    }
}
