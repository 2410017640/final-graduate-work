package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.common.BusinessException;
import com.smartrent.common.JwtUtil;
import com.smartrent.common.Role;
import com.smartrent.dto.LoginDTO;
import com.smartrent.dto.LoginVO;
import com.smartrent.dto.RegisterDTO;
import com.smartrent.entity.User;
import com.smartrent.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户业务：注册、登录
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    /** BCrypt 密码加密器 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 注册：校验参数 → 检查用户名 → 加密密码 → 保存
     */
    public void register(RegisterDTO dto) {
        // 1. 参数校验
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new BusinessException("密码长度至少 6 位");
        }
        // 2. 角色校验：注册只允许房东和房客，管理员由系统内置
        if (!Role.LANDLORD.equals(dto.getRole()) && !Role.TENANT.equals(dto.getRole())) {
            throw new BusinessException("注册角色只能是房东或房客");
        }
        // 3. 用户名唯一性检查
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername().trim()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 4. 保存用户，密码加密存储
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() == null || dto.getNickname().isEmpty()
                ? dto.getUsername().trim() : dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    /**
     * 登录：查用户 → 校验密码 → 校验状态 → 生成令牌
     */
    public LoginVO login(LoginDTO dto) {
        // 1. 按用户名查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        // 2. 校验密码（BCrypt 比对）
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        // 3. 校验账号状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        // 4. 生成令牌并返回
        LoginVO vo = new LoginVO();
        vo.setToken(JwtUtil.createToken(user.getId(), user.getUsername(), user.getRole()));
        vo.setUser(user);
        return vo;
    }

    /**
     * 根据 ID 查询用户（后续阶段会用到）
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
