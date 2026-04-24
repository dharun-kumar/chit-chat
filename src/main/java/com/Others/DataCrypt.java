package com.Others;

import java.util.Base64;

public class DataCrypt
{
    public static String encode(String data)
    {
        try{
            return Base64.getEncoder().encodeToString(data.getBytes());
        }
        catch (Exception e){
            return null;
        }
    }

    public static String decode(String data)
    {
        try{
            return new String(Base64.getDecoder().decode(data));
        }
        catch (Exception e){
            return null;
        }
    }
}
