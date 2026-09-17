package com.smartrent.service;

import com.smartrent.dto.FilterCondition;

/**
 * 自然语言解析器接口（"通用"设计）
 *
 * 当前实现：RuleNlParser（规则解析，无需外部 API）
 * 后续可选：LlmNlParser（接入大模型解析，配置 key 后切换）
 */
public interface NlParser {

    /**
     * 把一句自然语言解析成结构化筛选条件
     *
     * @param query 如"近地铁的两居室，4000以内"
     */
    FilterCondition parse(String query);
}
