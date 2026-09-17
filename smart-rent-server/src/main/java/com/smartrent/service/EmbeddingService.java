package com.smartrent.service;

/**
 * 向量化(embedding)接口 —— 把文本转成固定维度向量，供语义检索计算余弦相似度。
 *
 * "通用/可切换"设计：
 * - 当前实现：BigramEmbeddingService（字符二元组特征哈希，无需外部 API，零成本）。
 * - 后续配 key 后可新增 ApiEmbeddingService（调用真实 embedding 模型，如
 *   OpenAI text-embedding / 通义 / 智谱 / 本地模型），替换实现即可，
 *   SemanticSearchService 及其它调用方无需改动。
 */
public interface EmbeddingService {

    /**
     * 把文本转成已 L2 归一化的固定维度向量
     */
    double[] embed(String text);
}
