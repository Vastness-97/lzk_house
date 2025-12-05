package com.lzk.auth.service;

import com.lzk.auth.dto.LoginResponse;
import com.lzk.auth.dto.RegisterRequest;
import com.lzk.auth.exception.AuthException;
import com.lzk.common.constant.UserConstants;
import com.lzk.common.result.Result;
import com.lzk.common.util.JwtUtil;
import com.lzk.feign.UserFeignClient;
import com.lzk.feign.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserFeignClient userFeignClient;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(String username, String password) {
        log.info("用户登录请求: username={}", username);
        Result<UserDTO> result = userFeignClient.queryUser(username);
        UserDTO user = result.getData();
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            log.warn("登录失败: 用户名或密码错误, username={}", username);
            throw new AuthException("用户名或密码错误");
        }
        
        if (UserConstants.DELETED_YES.equals(user.getDeleted())) {
            log.warn("登录失败: 用户已被删除, username={}", username);
            throw new AuthException("用户不存在");
        }
        
        if (UserConstants.STATUS_DISABLED.equals(user.getStatus())) {
            log.warn("登录失败: 用户已被禁用, username={}", username);
            throw new AuthException("用户已被禁用");
        }
        
        log.info("用户登录成功: username={}, userId={}", username, user.getId());

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);
        response.getUser().setPassword(null);
        return response;
    }

    public boolean register(RegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());
        Result<Boolean> existsResult = userFeignClient.isUsernameExists(request.getUsername());
        if (existsResult.getData()) {
            log.warn("注册失败: 用户名已存在, username={}", request.getUsername());
            return false;
        }


        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(request.getUsername());
        userDTO.setPassword(passwordEncoder.encode(request.getPassword()));
        userDTO.setNickname(request.getNickname());
        userDTO.setEmail(request.getEmail());
        userDTO.setPhone(request.getPhone());
        userDTO.setDeleted(UserConstants.DELETED_NO);
        userDTO.setStatus(UserConstants.STATUS_NORMAL);
        userDTO.setCreateTime(LocalDateTime.now());
        userDTO.setUpdateTime(LocalDateTime.now());

        Result<Boolean> createResult = userFeignClient.createUser(userDTO);
        boolean success = createResult.getData();
        if (success) {
            log.info("用户注册成功: username={}", request.getUsername());
        }
        return success;
    }
}
