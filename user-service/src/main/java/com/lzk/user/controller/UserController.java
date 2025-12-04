package com.lzk.user.controller;

import com.lzk.common.result.Result;
import com.lzk.user.dto.LoginRequest;
import com.lzk.user.entity.User;
import com.lzk.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户名或密码错误");
    }
}
