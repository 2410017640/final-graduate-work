package com.smartrent.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 登录与角色拦截器：
 * 1. 除白名单外，所有请求必须带合法 token（登录校验）
 * 2. 方法上有 @RequireRole 注解时，校验当前用户角色是否匹配
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行非接口请求（如静态资源）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 从请求头取 token
        String token = request.getHeader("Authorization");
        Map<String, Object> payloads = null;
        if (token != null && !token.isEmpty()) {
            payloads = JwtUtil.parseToken(token);
        }

        // 未登录或令牌无效
        if (payloads == null) {
            writeError(response, 401, "未登录或登录已过期");
            return false;
        }

        // 保存当前用户信息，供后续代码使用
        LoginUserContext.set(payloads);

        // 校验角色注解
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole != null) {
            String currentRole = payloads.get("role") == null ? "" : payloads.get("role").toString();
            boolean allowed = false;
            for (String role : requireRole.value()) {
                if (role.equals(currentRole)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                writeError(response, 403, "没有权限执行该操作");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束清理 ThreadLocal
        LoginUserContext.clear();
    }

    /** 输出统一格式的错误 JSON */
    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(message);
        result.setCode(code);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
