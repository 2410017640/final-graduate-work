package com.smartrent.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.entity.User;
import com.smartrent.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 系统启动时自动创建内置管理员账号（admin / 123456）
 *
 * 为什么用代码创建而不是在 SQL 里写死：
 * 管理员密码需要 BCrypt 加密，而加密后的字符串很难手算。
 * 用代码在启动时生成，既安全又省事，且只会在账号不存在时创建一次。
 */
@Component
public class AdminInitRunner implements CommandLineRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminInitRunner(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (count == null || count == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("系统管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setCreateTime(LocalDateTime.now());
            userMapper.insert(admin);
            System.out.println("[SmartRent] 内置管理员账号已创建：admin / 123456");
        }
    }
}
