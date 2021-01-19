package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.mapper.BlockMapper;
import com.example.demo.service.UserManageService;
import com.example.demo.service.WebSocketService;
import com.example.demo.utils.NetworkUtil;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserManageService userManageService;

    @Autowired
    LocalPublicBlockchain localPublicBlockchain;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    LocalCooperation localCooperation;

    @Autowired
    BlockMapper blockMapper;

    @RequestMapping("/login")
    @ResponseBody
    public IResponse login(@RequestParam String identity, @RequestParam String password) {
        IResponse response;
        LocalUser u = userManageService.getUserByIdentity(identity);
        if (u != null && u.getPassword().equals(password)) {
            response = new IResponse(0, u);
            //设置本地用户
            localCooperation.setLocalUser(u);
            //设置本地节点隐私数据
            localCooperation.init(blockMapper.findAllInputTxs(), blockMapper.findAllOutputTxs());
        } else
            response = new IResponse(1, "UserName or Password is wrong");
        return response;
    }

    @RequestMapping("/register")
    public IResponse register(@RequestParam String userName, @RequestParam String identity, @RequestParam String password) {
        IResponse response;
        boolean b = userManageService.addUser(userName, password, identity, "description", "AI", "huge");
        if (b) {
            User u = userManageService.getUserByIdentity(identity);
            response = new IResponse(0, u);

//            //添加用户到区块链上
//            u.setIpAddress(NetworkUtil.getLocalAddress());
//            localPublicBlockchain.addUser(u);
//            webSocketService.broadcast(msg, localPublicBlockchain.getSockets());

        } else
            response = new IResponse(1, "Register failed");
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
            response = new IResponse(0, "result is empty");
        return response;
    }

    @RequestMapping("/getIndistinctUsers")   //模糊查询
    public IResponse getIndistinctUsers(@RequestParam String like) {
        IResponse response;
        List<User> userList;
        userList = userManageService.getAllUsersIndistinct(like);
        if (userList != null) {
            response = new IResponse(0, userList);
        } else
            response = new IResponse(0, "result is empty");
        return response;
    }

    @RequestMapping("/getUserByIdentity")
    public IResponse getUserByIdentity(@RequestParam String identity) {
        IResponse response;
        User user;
        user = userManageService.getUserByIdentity(identity);
        if (user != null) {
            response = new IResponse(0, user);
        } else
            response = new IResponse(0, "User not exists");
        return response;
    }

}
