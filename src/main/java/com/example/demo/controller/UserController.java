package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserManageService userManageService;

    @RequestMapping("/login")
    public IResponse login(@RequestParam String identity, @RequestParam String password) {
        IResponse response;
        User u = userManageService.getUserByIdentity(identity);
        if (u != null && u.getPassword().equals(password)) {
            response = new IResponse(0, u);
        } else
            response = new IResponse(0, "用户名或密码不正确");
        return response;
    }

    @RequestMapping("/register")
    public IResponse login(@RequestParam String userName, @RequestParam String identity, @RequestParam String password) {
        IResponse response;
        boolean b = userManageService.addUser(userName, password, identity, "description", "人工智能", "huge");
        if (b) {
            response = new IResponse(0, "注册成功");
        } else
            response = new IResponse(1, "注册失败");
        return response;
    }

    @RequestMapping("/getAllUsers")
    public IResponse getAllUsers() {
        IResponse response;
        List<User> userList;
        userList = userManageService.getAllUsers();
        if (userList != null) {
            response = new IResponse(0, userList);
        } else
            response = new IResponse(1, "查询失败");
        return response;
    }

    @RequestMapping("/getIndistinctUsers")   //模糊查询
    public IResponse getAllUsers(@RequestParam String like) {
        IResponse response;
        List<User> userList;
        userList = userManageService.getAllUsersIndistinct(like);
        if (userList != null) {
            response = new IResponse(0, userList);
        } else
            response = new IResponse(0, "没有找到符合条件的用户");
        return response;
    }

}
