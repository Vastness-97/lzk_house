package com.lzk.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "user-service")
public interface UserFeignClient {
    
    @GetMapping("/test/hello")
    String hello();
}
