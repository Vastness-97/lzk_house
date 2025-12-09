package com.lzk.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzk.common.result.Result;
import com.lzk.common.util.JwtUtil;
import com.lzk.gateway.exception.GatewayErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 认证过滤器
 * 对所有请求进行Token验证，白名单路径除外
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Resource
    private ObjectMapper objectMapper;

    /**
     * TODO：白名单优化，加入到配置文件中
     * 白名单路径，无需Token验证
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register"
    );

    /**
     * 过滤器核心逻辑
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("请求路径: {}", path);

        // 白名单放行
        if (isWhitePath(path)) {
            log.debug("白名单路径，直接放行: {}", path);
            return chain.filter(exchange);
        }

        // 从请求头获取Token
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            log.warn("Token为空，拒绝访问: {}", path);
            return unauthorized(exchange.getResponse(), GatewayErrorCode.TOKEN_EMPTY.getCode(),GatewayErrorCode.TOKEN_EMPTY.getMsg());
        }
        // 验证Token有效性
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            // 解析并验证Token
            JwtUtil.parseToken(token);
            log.debug("Token验证通过: {}", path);
            return chain.filter(exchange);
        } catch (ExpiredJwtException e) {
            log.warn("Token验证失败: {}, error: {}", path, GatewayErrorCode.TOKEN_EXPIRED.getMsg());
            return unauthorized(exchange.getResponse(), GatewayErrorCode.TOKEN_EXPIRED.getCode(),GatewayErrorCode.TOKEN_EXPIRED.getMsg());
        } catch (JwtException e) {
            log.warn("Token验证失败: {}, error: {}", path, GatewayErrorCode.TOKEN_INVALID.getMsg());
            return unauthorized(exchange.getResponse(), GatewayErrorCode.TOKEN_INVALID.getCode(),GatewayErrorCode.TOKEN_INVALID.getMsg());
        }
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * TODO: 封装一个JACKSON的工具类
     * 返回401未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, int code,String msg) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(Result.error(code, msg));
            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return response.setComplete();
        }
    }

    /**
     * 过滤器执行顺序，数值越小优先级越高
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
