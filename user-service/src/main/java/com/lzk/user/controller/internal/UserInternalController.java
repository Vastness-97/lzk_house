package com.lzk.user.controller.internal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzk.common.result.Result;
import com.lzk.user.entity.User;
import com.lzk.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 内部调用controller
 */
@RestController
@RequestMapping("/internal/user")
public class UserInternalController {
    
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/query")
    public Result<User> queryUser(@RequestParam("username") String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        return Result.success(user);
    }

    @PostMapping("/create")
    public Result<Boolean> createUser(@RequestBody User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        int rows = userMapper.insert(user);
        return Result.success(rows > 0);
    }

    @GetMapping("/exists")
    public Result<Boolean> isUsernameExists(@RequestParam("username") String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        long count = userMapper.selectCount(wrapper);
        return Result.success(count > 0);
    }
}
