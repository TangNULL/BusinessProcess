package com.example.demo.entity;

import org.springframework.stereotype.Component;

public class LocalUser extends User{
    private String password;  //本地的用户密码

    public LocalUser(String username, String password, String identity, String description, String coreBusiness, String assessment) {
        super(username, identity, description, coreBusiness, assessment);
        this.password = password;
    }

    public LocalUser(Integer userid, String username,  String password, String identity, String description, String coreBusiness, String assessment) {
        super(userid, username, identity, description, coreBusiness, assessment);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
