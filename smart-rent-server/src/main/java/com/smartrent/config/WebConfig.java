package com.smartrent.config;

import com.smartrent.common.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：注册登录拦截器，并设置白名单（不需要登录就能访问的接口）
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                // 拦截所有接口
                .addPathPatterns("/**")
                // 白名单：注册、登录、测试接口不需要登录
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/test/**"
                );
    }
}
