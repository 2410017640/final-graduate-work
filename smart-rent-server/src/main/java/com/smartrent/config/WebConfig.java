package com.smartrent.config;

import com.smartrent.common.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：注册登录拦截器、白名单、跨域 CORS
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

    /** 允许前端跨域访问（前端用 Vite 代理时其实不需要，这里作兜底） */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
