package com.lzk.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzk.common.result.Result;
import com.lzk.user.entity.User;
import com.lzk.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户控制器
 * 提供用户基本信息的内部接口，供Feign调用
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询用户信息（内部接口）
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @GetMapping("/query")
    public Result<User> queryUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        log.debug("查询用户: username={}", username);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getPassword, password);
        User user = userMapper.selectOne(wrapper);
        log.debug("查询结果: user={}", user != null ? user.getUsername() : "null");
        return Result.success(user);
    }

    /**
     * 创建用户（内部接口）
     * @param user 用户信息
     * @return 创建是否成功
     */
    @PostMapping("/create")
    public Result<Boolean> createUser(@RequestBody User user) {
        log.info("创建用户: username={}", user.getUsername());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        int rows = userMapper.insert(user);
        log.info("用户创建结果: username={}, success={}", user.getUsername(), rows > 0);
        return Result.success(rows > 0);
    }

    /**
     * 检查用户名是否存在（内部接口）
     * @param username 用户名
     * @return 是否存在
     */
    @GetMapping("/exists")
    public Result<Boolean> isUsernameExists(@RequestParam("username") String username) {
        log.debug("检查用户名是否存在: username={}", username);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        long count = userMapper.selectCount(wrapper);
        log.debug("用户名存在检查结果: username={}, exists={}", username, count > 0);
        return Result.success(count > 0);
    }
}
