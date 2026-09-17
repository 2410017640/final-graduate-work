package com.smartrent.dto;

import lombok.Data;

/**
 * AI 生成标签时接收的参数
 */
@Data
public class TagSuggestDTO {
    private String title;
    private String description;
}
