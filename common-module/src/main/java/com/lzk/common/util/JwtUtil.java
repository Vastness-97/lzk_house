package com.lzk.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 * 提供JWT token的生成、解析、验证等功能
 */
public class JwtUtil {
    
    /** JWT签名密钥 */
    private static final String SECRET = "mySecretKeyForJWT1234567890123456789012345678901234567890";
    
    /** token过期时间：24小时 */
    private static final long EXPIRATION = 86400000;
    
    /** 签名密钥对象 */
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT token字符串
     */
    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(userId.toString())  // 设置主题为用户ID
                .claim("username", username)    // 添加用户名声明
                .setIssuedAt(new Date())        // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))  // 设置过期时间
                .signWith(KEY, SignatureAlgorithm.HS256)  // 使用HS256算法签名
                .compact();
    }

    /**
     * 解析JWT token获取Claims
     * @param token JWT token
     * @return Claims对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)  // 设置签名密钥
                .build()
                .parseClaimsJws(token)  // 解析token
                .getBody();
    }

    /**
     * 检查token是否过期
     * @param token JWT token
     * @return true-已过期，false-未过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // 解析异常也认为token无效
            return true;
        }
    }

    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从token中获取用户名
     * @param token JWT token
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
}