package com.lzk.auth.controller;

import com.lzk.auth.dto.LoginRequest;
import com.lzk.auth.dto.LoginResponse;
import com.lzk.auth.dto.RegisterRequest;
import com.lzk.auth.service.AuthService;
import com.lzk.common.result.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        boolean success = authService.register(request);
        if (success) {
            return Result.success("注册成功");
        } else {
            return Result.error("用户名已存在");
        }
    }
}
