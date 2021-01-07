package com.example.demo.mapper;

import com.example.demo.entity.LocalUser;
import com.example.demo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    //个人用户，用户名和密码保存在本地，登录系统使用
    LocalUser findUserByIdentity(String iden);

    User findUserById(Integer id);

    Integer insertUser(LocalUser user);

    List<User> findAllUsers();

    List<User> findUserLike(String s);

    List<User> findUsersByBPId(int businessProcessId);

}
