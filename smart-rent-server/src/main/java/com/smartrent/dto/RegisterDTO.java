package com.smartrent.dto;

import lombok.Data;

/**
 * 注册请求参数
 */
@Data
public class RegisterDTO {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 角色：LANDLORD 房东 / TENANT 房客（注册只允许这两种，管理员由系统内置） */
    private String role;
}
