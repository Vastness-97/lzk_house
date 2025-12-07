package com.lzk.feign.fallback;

import com.lzk.common.exception.SystemErrorCode;
import com.lzk.common.result.Result;
import com.lzk.feign.UserFeignClient;
import com.lzk.feign.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClientFallback implements UserFeignClient {

    private static final Logger log = LoggerFactory.getLogger(UserFeignClientFallback.class);


    @Override
    public Result<UserDTO> queryUser(String username) {
        log.error("【Fallback】调用 user-service 的 queryUser 失败，服务不可用");
        return Result.error(SystemErrorCode.SERVICE_UNAVAILABLE);
    }

    @Override
    public Result<Boolean> createUser(UserDTO userDTO) {
        log.error("【Fallback】调用 user-service 的 createUser 失败，服务不可用");
        return Result.error(SystemErrorCode.SERVICE_UNAVAILABLE);
    }

    @Override
    public Result<Boolean> isUsernameExists(String username) {
        log.error("【Fallback】调用 user-service 的 isUsernameExists 失败，服务不可用");
        return Result.error(SystemErrorCode.SERVICE_UNAVAILABLE);
    }
}
