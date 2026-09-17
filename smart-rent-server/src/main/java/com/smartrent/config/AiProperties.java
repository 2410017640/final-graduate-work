package com.smartrent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AI 大模型配置（"通用"设计：换 DeepSeek / OpenAI / 通义等 OpenAI 兼容接口，
 * 只需改 base-url、model、api-key，无需改代码）
 */
@Component
public class AiProperties {

    /** 接口地址（OpenAI 兼容） */
    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    /** API Key，优先读环境变量 DEEPSEEK_API_KEY；为空表示未配置，走规则降级 */
    @Value("${ai.deepseek.api-key:}")
    private String apiKey;

    /** 模型名，如 deepseek-chat */
    @Value("${ai.deepseek.model:deepseek-chat}")
    private String model;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    /** 是否已配置 API Key（未配置则 AI 功能自动降级为规则） */
    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }
}
