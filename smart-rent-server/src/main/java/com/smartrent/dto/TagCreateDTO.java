package com.smartrent.dto;

import lombok.Data;

/**
 * 管理员创建标签时接收的参数
 */
@Data
public class TagCreateDTO {
    /** 标签名称（唯一） */
    private String name;
    /** 分组，如：交通/户型/租约/设施/人群（可空） */
    private String category;
}
