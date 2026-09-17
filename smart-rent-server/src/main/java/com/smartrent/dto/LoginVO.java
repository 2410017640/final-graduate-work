package com.smartrent.dto;

import com.smartrent.entity.User;
import lombok.Data;

/**
 * 登录成功后返回给前端的信息
 */
@Data
public class LoginVO {

    /** JWT 令牌，前端保存后每次请求带上 */
    private String token;

    /** 用户信息（密码已被 @JsonIgnore 排除，不会泄露） */
    private User user;
}
