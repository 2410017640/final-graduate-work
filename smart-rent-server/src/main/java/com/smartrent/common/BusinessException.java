package com.smartrent.common;

/**
 * 业务异常：当业务规则不满足时抛出
 * 例如：用户名已存在、用户名或密码错误、账号被禁用等。
 *
 * 抛出后会被 GlobalExceptionHandler 捕获，统一转成 Result 格式返回给前端，
 * 这样前端拿到的永远是一致的结构，不会出现 Spring 默认的错误页。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
