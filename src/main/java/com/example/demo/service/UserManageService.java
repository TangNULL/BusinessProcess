package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserManageService {
    User getUserByIdentity(String identity);   //根据身份凭证查找User

    boolean addUser(String username, String password, String identity, String description, String coreBusiness, String assessment);  //注册用户

    List<User> getAllUsers();  //获得所有用户列表
}
