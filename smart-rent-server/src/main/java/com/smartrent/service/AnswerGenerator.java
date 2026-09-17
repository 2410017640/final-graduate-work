package com.smartrent.service;

import com.smartrent.entity.Knowledge;

import java.util.List;

/**
 * 回答生成器接口 —— 根据"问题 + 检索到的相关知识"生成最终回答（RAG 的生成阶段）。
 *
 * "通用/可切换"设计：
 * - 当前实现 DirectAnswerGenerator：直接组合检索到的知识，无需外部 API。
 * - 后续配 key 后可新增 LlmAnswerGenerator（把检索到的知识作为上下文喂给大模型生成自然回答）。
 */
public interface AnswerGenerator {

    /**
     * 生成回答
     *
     * @param question 租客原始问题
     * @param context  检索到的相关知识（已按相关度排序）
     * @return 回答文本；无可用知识返回 null（触发转交房东）
     */
    String generate(String question, List<Knowledge> context);
}
