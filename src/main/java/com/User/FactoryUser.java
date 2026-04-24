package com.User;

public class FactoryUser
{
    public static UserOperation getInstance(String database)
    {
        if(database.equals("ES"))
            return new ESUserOperation();

       else if(database.equals("SQL"))
           return new SQLUserOperation();

        else
            return null;
    }
}
