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
        return userMapper.findUserByIdentity(identity);
    }

    @Override
    public boolean addUser(String username, String password, String identity, String description, String coreBusiness, String assessment) {
        User u = new User(username, password, identity, description, coreBusiness, assessment);
        Integer userid = userMapper.insertUser(u);
        return userid != null && userid > 0;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAllUsers();
    }

    @Override
    public List<User> findUsersByBPId(int bpId) {
        return userMapper.findUsersByBPId(bpId);
    }

    @Override
    public List<User> getAllUsersIndistinct(String s) {
        return userMapper.findUserLike(s);
    }
}
