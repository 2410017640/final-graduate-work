package com.smartrent.dto;

import com.smartrent.entity.House;
import lombok.Data;

import java.util.List;

/**
 * 语义检索结果：房源 + 命中的特征词 + 是否含非预制标签
 */
@Data
public class SemanticMatchVO {

    /** 房源 */
    private House house;

    /** 命中的特征词（如"池塘"、"钓鱼"），可能不是预置标签 */
    private List<String> matchedFeatures;

    /** 是否含"非预置标签"命中的特征（前端据此在筛选框特殊备注） */
    private boolean nonPresetMatched;

    /** 向量相似度分数 */
    private double score;
}
