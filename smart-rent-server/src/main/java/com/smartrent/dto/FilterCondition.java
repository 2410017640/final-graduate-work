package com.smartrent.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 自然语言筛选解析出的结构化条件
 */
@Data
public class FilterCondition {

    /** 命中的标签ID列表（全部满足） */
    private List<Long> tagIds;

    /** 室数，如"两居室" -> 2 */
    private Integer roomCount;

    /** 租金下限/上限，如"4000以内" -> maxRent=4000 */
    private BigDecimal minRent;
    private BigDecimal maxRent;

    /** 面积下限，如"60平米" -> minArea=60 */
    private BigDecimal minArea;

    /** 剩余关键词（没有任何结构化条件时才作为整句关键词） */
    private String keyword;
}
