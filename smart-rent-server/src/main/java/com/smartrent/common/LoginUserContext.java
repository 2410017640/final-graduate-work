package com.smartrent.common;

import java.util.Map;

/**
 * 当前登录用户上下文：
 * 拦截器解析 token 后，把用户信息存进 ThreadLocal，
 * 后续 Controller/Service 随时可以取到"当前是谁在操作"
 */
public class LoginUserContext {

    private static final ThreadLocal<Map<String, Object>> HOLDER = new ThreadLocal<>();

    /** 拦截器调用：保存当前用户信息 */
    public static void set(Map<String, Object> payloads) {
        HOLDER.set(payloads);
    }

    /** 拦截器调用：请求结束后清理，防止内存泄漏 */
    public static void clear() {
        HOLDER.remove();
    }

    /** 获取当前登录用户ID，未登录返回 null */
    public static Long getUserId() {
        Map<String, Object> payloads = HOLDER.get();
        if (payloads == null || payloads.get("userId") == null) {
            return null;
        }
        return Long.parseLong(payloads.get("userId").toString());
    }

    /** 获取当前登录用户名，未登录返回 null */
    public static String getUsername() {
        Map<String, Object> payloads = HOLDER.get();
        if (payloads == null || payloads.get("username") == null) {
            return null;
        }
        return payloads.get("username").toString();
    }

    /** 获取当前登录用户角色，未登录返回 null */
    public static String getRole() {
        Map<String, Object> payloads = HOLDER.get();
        if (payloads == null || payloads.get("role") == null) {
            return null;
        }
        return payloads.get("role").toString();
    }
}
