package com.lzk.auth.service;

import com.lzk.auth.dto.response.LoginResponse;
import com.lzk.auth.dto.request.RegisterRequest;
import com.lzk.auth.exception.AuthErrorCode;
import com.lzk.auth.exception.AuthException;
import com.lzk.common.constant.UserConstants;
import com.lzk.common.enums.DeleteStatus;
import com.lzk.common.enums.UserStatus;
import com.lzk.common.result.Result;
import com.lzk.common.util.JwtUtil;
import com.lzk.feign.PermissionFeignClient;
import com.lzk.feign.UserFeignClient;
import com.lzk.feign.dto.UserDTO;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务
 * 处理用户登录、注册等业务逻辑
 */
@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserFeignClient userFeignClient;
    
    @Autowired
    private PermissionFeignClient permissionFeignClient;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录响应（包含token和用户信息）
     */
    public LoginResponse login(String username, String password) {
        log.info("用户登录请求: username={}", username);
        Result<UserDTO> result = userFeignClient.queryUser(username);
        UserDTO user = result.getData();
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            log.warn("登录失败: 用户名或密码错误, username={}", username);
            throw new AuthException(AuthErrorCode.ACCOUNT_OR_PASSWORD_ERROR);
        }
        
        if (UserConstants.DELETED_YES.equals(user.getDeleted())) {
            log.warn("登录失败: 用户已被删除, username={}", username);
            throw new AuthException(AuthErrorCode.ACCOUNT_DELETED);
        }
        
        if (UserConstants.STATUS_DISABLED.equals(user.getStatus())) {
            log.warn("登录失败: 用户已被禁用, username={}", username);
            throw new AuthException(AuthErrorCode.ACCOUNT_DISABLED);
        }
        
        log.info("用户登录成功: username={}, userId={}", username, user.getId());

        // 获取用户权限列表
        List<String> permissions = permissionFeignClient.getPermissionCodesByUserId(user.getId());
        log.debug("用户权限: userId={}, permissions={}", user.getId(), permissions);
        
        // 生成JWT token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), permissions);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);
        response.getUser().setPassword(null);
        return response;
    }

    /**
     * 用户注册
     * @param request 注册请求参数
     * @return 注册是否成功
     */
    public boolean register(RegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());
        
        // 检查用户名是否已存在
        Result<Boolean> existsResult = userFeignClient.isUsernameExists(request.getUsername());
        if (existsResult.getData()) {
            log.warn("注册失败: 账号已存在, username={}", request.getUsername());
            throw  new AuthException(AuthErrorCode.ACCOUNT_EXISTS);
        }


        // 构建用户对象
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(request.getUsername());
        // 密码加密
        userDTO.setPassword(passwordEncoder.encode(request.getPassword()));
        userDTO.setNickname(request.getNickname());
        userDTO.setEmail(request.getEmail());
        userDTO.setPhone(request.getPhone());
        userDTO.setDeleted(DeleteStatus.NOT_DELETED.getCode());
        userDTO.setStatus(UserStatus.NORMAL.getCode());
        userDTO.setCreateTime(LocalDateTime.now());
        userDTO.setUpdateTime(LocalDateTime.now());

        // 调用用户服务创建用户
        Result<Boolean> createResult = userFeignClient.createUser(userDTO);
        boolean success = createResult.getData();
        if (success) {
            log.info("用户注册成功: username={}", request.getUsername());
        }
        return success;
    }
}
