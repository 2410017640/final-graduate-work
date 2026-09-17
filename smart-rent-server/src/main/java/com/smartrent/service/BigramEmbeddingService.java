package com.smartrent.service;

import org.springframework.stereotype.Component;

/**
 * 轻量向量化实现：字符二元组特征哈希到固定维度向量，再做 L2 归一化。
 * 无需外部 embedding API，效果可控、可解释；后续可被 ApiEmbeddingService 替换。
 */
@Component
public class BigramEmbeddingService implements EmbeddingService {

    /** 向量维度（固定） */
    private static final int DIM = 256;

    @Override
    public double[] embed(String text) {
        double[] vec = new double[DIM];
        String t = text == null ? "" : text;
        // 字符二元组 -> 特征哈希到固定维度
        for (int i = 0; i + 1 < t.length(); i++) {
            int hash = (t.charAt(i) * 31 + t.charAt(i + 1)) & 0x7fffffff;
            vec[hash % DIM] += 1.0;
        }
        // L2 归一化，使余弦相似度等于点积
        double norm = 0;
        for (double v : vec) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < DIM; i++) {
                vec[i] /= norm;
            }
        }
        return vec;
    }
}
