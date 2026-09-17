package com.smartrent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能租房系统启动类
 */
@SpringBootApplication
// 告诉 MyBatis-Plus 去哪个包下找 Mapper 接口（第二阶段开始使用）
@MapperScan("com.smartrent.mapper")
public class SmartRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartRentApplication.class, args);
    }
}
