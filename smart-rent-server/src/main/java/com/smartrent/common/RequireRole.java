package com.smartrent.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解：加在 Controller 方法上，限制只有指定角色能访问
 * 例如：@RequireRole(Role.LANDLORD) 表示只有房东能调用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** 允许访问的角色列表 */
    String[] value();
}
