package com.lzk.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", path = "/permission")
public interface PermissionFeignClient {
    
    @GetMapping("/user/{userId}")
    List<String> getPermissionCodesByUserId(@PathVariable("userId") Long userId);
}
