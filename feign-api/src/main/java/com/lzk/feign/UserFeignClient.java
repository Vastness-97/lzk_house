package com.lzk.feign;

import com.lzk.common.result.Result;
import com.lzk.feign.dto.UserDTO;
import com.lzk.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "user-service",fallback = UserFeignClientFallback.class)
@RequestMapping("/internal/user")
public interface UserFeignClient {
    
    @GetMapping("query")
    Result<UserDTO> queryUser(@RequestParam("username") String username);
    
    @PostMapping("create")
    Result<Boolean> createUser(@RequestBody UserDTO userDTO);
    
    @GetMapping("exists")
    Result<Boolean> isUsernameExists(@RequestParam("username") String username);
}
