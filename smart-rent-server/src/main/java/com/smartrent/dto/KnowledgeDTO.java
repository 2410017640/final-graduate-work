package com.smartrent.dto;

import lombok.Data;

/**
 * 知识库条目 新增/修改时接收的参数
 */
@Data
public class KnowledgeDTO {
    private String question;
    private String answer;
    private String category;
}
