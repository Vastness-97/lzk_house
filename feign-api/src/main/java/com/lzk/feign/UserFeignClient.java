package com.lzk.feign;

import com.lzk.common.result.Result;
import com.lzk.feign.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface UserFeignClient {
    
    @GetMapping("/test/hello")
    String hello();
    
    @GetMapping("/user/query")
    Result<UserDTO> queryUser(@RequestParam("username") String username);
    
    @PostMapping("/user/create")
    Result<Boolean> createUser(@RequestBody UserDTO userDTO);
    
    @GetMapping("/user/exists")
    Result<Boolean> isUsernameExists(@RequestParam("username") String username);
}
