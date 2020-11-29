package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    User findUserByIdentity(String iden);

    User findUserById(Integer id);

    Integer insertUser(User user);

    List<User> findAllUsers();

    List<User> findUserLike(String s);

    List<User> findUsersByBPId(int businessProcessId);

}
