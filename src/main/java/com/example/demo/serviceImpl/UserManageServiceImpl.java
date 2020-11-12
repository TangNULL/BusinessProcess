package com.example.demo.serviceImpl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManageServiceImpl implements UserManageService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUserByIdentity(String identity) {
        return userMapper.getUserByIdentity(identity);
    }

    @Override
    public boolean addUser(String username, String password, String identity, String description, String coreBusiness, String assessment) {
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
