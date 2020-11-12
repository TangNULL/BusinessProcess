package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserManageService userManageService;

    @RequestMapping("/login")
    public User login(@RequestParam String i, @RequestParam String p) {
        User u = userManageService.getUserByIdentity(i);
        if (u != null && u.getPassword().equals(p)) {
            return u;
        }
        return null;
    }
}
