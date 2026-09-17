package com.smartrent.dto;

import lombok.Data;

/**
 * 管理员修改标签时接收的参数
 */
@Data
public class TagUpdateDTO {
    private String name;
    private String category;
}
