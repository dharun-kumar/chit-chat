package com.chitchat.repo.user;

public interface UserRepo {
    void getConnection();
    boolean userLogin(String uname, String password);
    boolean addUser(String uname, String password);
    boolean userExist(String uname);
    void setKey(String uname, String sessionkey);
    boolean checkKey(String uname, String sessionkey);
}
