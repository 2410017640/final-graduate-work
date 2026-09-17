package com.smartrent.controller;

import com.smartrent.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;

/**
 * 测试接口：用于验证项目启动和数据库连接是否正常
 * 第一阶段完成后可以删除或保留，不影响业务
 */
@RestController
public class TestController {

    // Spring 自动注入数据库连接池
    private final DataSource dataSource;

    public TestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 验证项目是否启动成功
     * 浏览器访问：http://localhost:8080/test/hello
     */
    @GetMapping("/test/hello")
    public Result<String> hello() {
        return Result.success("智能租房系统后端启动成功，当前时间：" + LocalDateTime.now());
    }

    /**
     * 验证数据库是否连接成功
     * 浏览器访问：http://localhost:8080/test/db
     */
    @GetMapping("/test/db")
    public Result<String> testDb() {
        try (Connection conn = dataSource.getConnection()) {
            return Result.success("数据库连接成功，数据库名：" + conn.getCatalog());
        } catch (Exception e) {
            return Result.error("数据库连接失败：" + e.getMessage());
        }
    }
}
