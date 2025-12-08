package com.lzk.auth.controller;

import com.lzk.auth.dto.request.LoginRequest;
import com.lzk.auth.dto.response.LoginResponse;
import com.lzk.auth.dto.request.RegisterRequest;
import com.lzk.auth.service.AuthService;
import com.lzk.common.result.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关请求
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param request 登录请求参数（用户名、密码）
     * @return 登录响应（token和用户信息）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求: username={}", request.getUsername());
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        log.info("登录成功: username={}", request.getUsername());
        return Result.success(response);
    }

    /**
     * 用户注册
     * @param request 注册请求参数（用户名、密码、昵称等）
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        log.info("收到注册请求: username={}", request.getUsername());
        authService.register(request);
        log.info("注册成功: username={}", request.getUsername());
        return Result.success("注册成功");
    }
}
