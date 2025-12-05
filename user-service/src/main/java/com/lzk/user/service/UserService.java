package com.lzk.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzk.common.constant.UserConstants;
import com.lzk.common.util.JwtUtil;
import com.lzk.user.dto.LoginResponse;
import com.lzk.user.dto.RegisterRequest;
import com.lzk.user.entity.User;
import com.lzk.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserMapper userMapper;

    public LoginResponse login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getPassword, password);
        User user = userMapper.selectOne(wrapper);

        //非空判断，如果user为空，则用户名或密码错误
        if (user == null) {
            return null;
        }

        String token = generateToken(user);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);
        response.getUser().setPassword(null);
        return response;
    }

    public boolean register(RegisterRequest request) {
        log.info("注册用户: username={}", request.getUsername());
        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            log.warn("用户名已存在: username={}", request.getUsername());
            return false;
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDeleted(UserConstants.DELETED_NO);
        user.setStatus(UserConstants.STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        boolean success = userMapper.insert(user) > 0;
        if (success) {
            log.info("用户注册成功: username={}, userId={}", request.getUsername(), user.getId());
        }
        return success;
    }

    public boolean isUsernameExists(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    public String generateToken(User user) {
        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
