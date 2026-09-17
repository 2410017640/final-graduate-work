package com.smartrent.service;

import com.smartrent.entity.Knowledge;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 直接回答生成器（RAG 生成阶段的轻量实现，无需大模型）：
 * 把检索到的相关知识按序拼接成一条回答；无知识返回 null。
 * 后续配 key 后可被 LlmAnswerGenerator 替换。
 */
@Component
public class DirectAnswerGenerator implements AnswerGenerator {

    @Override
    public String generate(String question, List<Knowledge> context) {
        if (context == null || context.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < context.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append(context.get(i).getAnswer());
        }
        return sb.toString();
    }
}
