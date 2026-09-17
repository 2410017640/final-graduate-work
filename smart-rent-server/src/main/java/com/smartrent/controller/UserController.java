package com.smartrent.controller;

import com.smartrent.common.LoginUserContext;
import com.smartrent.common.RequireRole;
import com.smartrent.common.Result;
import com.smartrent.common.Role;
import com.smartrent.dto.LoginDTO;
import com.smartrent.dto.LoginVO;
import com.smartrent.dto.RegisterDTO;
import com.smartrent.entity.User;
import com.smartrent.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口：注册、登录、获取当前用户信息
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册（白名单，不需要登录）
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    /**
     * 登录（白名单，不需要登录）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /**
     * 获取当前登录用户信息（需要登录，用于验证拦截器）
     */
    @GetMapping("/me")
    public Result<User> me() {
        Long userId = LoginUserContext.getUserId();
        return Result.success(userService.getById(userId));
    }

    /**
     * 管理员权限验证接口（仅用于测试角色权限拦截器是否生效）
     * 只有 ADMIN 角色能访问，房东/房客访问会返回 403 无权限
     */
    @GetMapping("/admin/test")
    @RequireRole(Role.ADMIN)
    public Result<String> adminTest() {
        return Result.success("管理员权限验证通过");
    }
}
