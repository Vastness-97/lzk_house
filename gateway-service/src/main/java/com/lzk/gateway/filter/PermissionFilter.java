package com.lzk.gateway.filter;

import com.lzk.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 权限过滤器
 * 对请求进行权限验证，检查用户是否有访问资源的权限
 */
@Component
public class PermissionFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(PermissionFilter.class);
    
    /** 白名单路径，无需权限验证 */
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/auth/login",
        "/auth/register"
    );

    /**
     * 权限过滤器核心逻辑
     * @param exchange 请求上下文
     * @param chain 过滤器链
     * @return Mono响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // 白名单直接放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Token
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            // 从Token中解析权限列表
            List<String> permissions = JwtUtil.getPermissions(token);
            if (permissions == null || permissions.isEmpty()) {
                log.warn("用户无任何权限: path={}", path);
                return forbidden(exchange.getResponse());
            }
            
            // 检查是否有访问权限
            if (hasPermission(path, method, permissions)) {
                log.debug("权限验证通过: path={}, method={}", path, method);
                return chain.filter(exchange);
            } else {
                log.warn("权限不足: path={}, method={}, permissions={}", path, method, permissions);
                return forbidden(exchange.getResponse());
            }
        } catch (Exception e) {
            log.error("权限验证异常: {}", e.getMessage());
            return chain.filter(exchange);
        }
    }
    
    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }
    
    /**
     * 检查用户是否有访问权限
     * @param path 请求路径
     * @param method 请求方法
     * @param permissions 用户权限列表
     * @return 是否有权限
     */
    private boolean hasPermission(String path, String method, List<String> permissions) {
        for (String permission : permissions) {
            if (permission.equals("*:*") || permission.equals("ROLE_ADMIN")) {
                return true;
            }
            if (matchPermission(path, method, permission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 匹配权限编码
     * @param path 请求路径
     * @param method 请求方法
     * @param permission 权限编码
     * @return 是否匹配
     */
    private boolean matchPermission(String path, String method, String permission) {
        if (path.startsWith("/user") && permission.startsWith("user:")) {
            return matchMethod(method, permission);
        }
        if (path.startsWith("/role") && permission.startsWith("role:")) {
            return matchMethod(method, permission);
        }
        if (path.startsWith("/permission") && permission.startsWith("permission:")) {
            return matchMethod(method, permission);
        }
        return false;
    }
    
    /**
     * 匹配HTTP方法与权限操作
     * @param method HTTP方法
     * @param permission 权限编码
     * @return 是否匹配
     */
    private boolean matchMethod(String method, String permission) {
        if (permission.endsWith(":manage")) return true;
        if (method.equals("GET") && permission.endsWith(":query")) return true;
        if (method.equals("POST") && permission.endsWith(":add")) return true;
        if (method.equals("PUT") && permission.endsWith(":update")) return true;
        if (method.equals("DELETE") && permission.endsWith(":delete")) return true;
        return false;
    }
    
    /**
     * 返回403禁止访问响应
     */
    private Mono<Void> forbidden(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 过滤器执行顺序，在认证过滤器之后执行
     */
    @Override
    public int getOrder() {
        return -99;
    }
}
