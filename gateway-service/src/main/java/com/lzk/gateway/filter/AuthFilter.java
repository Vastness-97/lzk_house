package com.lzk.gateway.filter;

import com.lzk.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/auth/login",
        "/auth/register"
    );

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
        
        // 获取token
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            log.warn("Token为空，拒绝访问: {}", path);
            return unauthorized(exchange.getResponse());
        }
        
        // 验证token
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            JwtUtil.parseToken(token);
            log.debug("Token验证通过: {}", path);
            return chain.filter(exchange);
        } catch (Exception e) {
            log.warn("Token验证失败: {}, error: {}", path, e.getMessage());
            return unauthorized(exchange.getResponse());
        }
    }
    
    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }
    
    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
