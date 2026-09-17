package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应数据库 user 表
 */
@Data
@TableName("user")
public class User {

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名，唯一 */
    private String username;

    /** 密码（BCrypt 加密后存储）；@JsonIgnore 保证密码永远不会返回给前端 */
    @JsonIgnore
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 角色：LANDLORD 房东 / TENANT 房客 / ADMIN 管理员 */
    private String role;

    /** 状态：1 正常 / 0 禁用 */
    private Integer status;

    /** 注册时间 */
    private LocalDateTime createTime;
}
