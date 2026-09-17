package com.smartrent.common;

import cn.hutool.jwt.JWTUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌工具类：生成令牌、解析令牌
 * 令牌里只放 用户ID、用户名、角色，不放密码
 */
public class JwtUtil {

    /** 签名密钥（真实项目应放在配置文件中，这里为简单直接写死） */
    private static final String SECRET = "smart-rent-secret-key-2026";

    /** 令牌有效期：7 天（毫秒） */
    private static final long EXPIRE_MILLIS = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成令牌
     */
    public static String createToken(Long userId, String username, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("role", role);
        payload.put("expire", System.currentTimeMillis() + EXPIRE_MILLIS);
        return JWTUtil.createToken(payload, SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析令牌，返回载荷；令牌无效或过期返回 null
     */
    public static Map<String, Object> parseToken(String token) {
        try {
            if (!JWTUtil.verify(token, SECRET.getBytes(StandardCharsets.UTF_8))) {
                return null;
            }
            Map<String, Object> payloads = JWTUtil.parseToken(token).getPayloads();
            Object expire = payloads.get("expire");
            if (expire == null || Long.parseLong(expire.toString()) < System.currentTimeMillis()) {
                return null;
            }
            return payloads;
        } catch (Exception e) {
            return null;
        }
    }

    private JwtUtil() {
    }
}
