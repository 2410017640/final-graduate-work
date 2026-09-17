package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartrent.config.AiProperties;
import com.smartrent.entity.Tag;
import com.smartrent.mapper.TagMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 服务：通用大模型调用 + 租房标签智能推荐
 *
 * 设计要点：
 * 1. base-url / model / api-key 全部走配置（AiProperties），换 provider 只改配置。
 * 2. 未配置 api-key 时自动降级为"关键词规则"推荐，保证无外部依赖也能演示、答辩稳定。
 */
@Service
public class AiService {

    private final AiProperties aiProperties;
    private final TagMapper tagMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(AiProperties aiProperties, TagMapper tagMapper) {
        this.aiProperties = aiProperties;
        this.tagMapper = tagMapper;
    }

    /**
     * 根据房源标题/描述，从标签库中推荐最合适的标签
     *
     * @return 标签库中命中的 Tag 列表（不含自创的新标签）
     */
    public List<Tag> suggestTags(String title, String description) {
        List<Tag> all = tagMapper.selectList(new LambdaQueryWrapper<Tag>());
        if (all.isEmpty()) {
            return new ArrayList<>();
        }
        String text = (title == null ? "" : title) + " " + (description == null ? "" : description);
        List<String> names = aiProperties.isEnabled()
                ? suggestByAi(all, title, description)
                : suggestByRule(all, text);

        // 名字 -> 标签，过滤出标签库里真实存在的
        Map<String, Tag> nameMap = new HashMap<>();
        for (Tag t : all) {
            nameMap.put(t.getName(), t);
        }
        List<Tag> result = new ArrayList<>();
        for (String n : names) {
            Tag t = nameMap.get(n);
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    /** 规则降级：标签名出现在房源文本中即命中（简单、可预期） */
    private List<String> suggestByRule(List<Tag> all, String text) {
        List<String> hit = new ArrayList<>();
        for (Tag t : all) {
            if (t.getName() != null && text.contains(t.getName())) {
                hit.add(t.getName());
            }
        }
        return hit;
    }

    /** 真实 AI：让大模型从给定标签库中挑选，只输出标签名 */
    private List<String> suggestByAi(List<Tag> all, String title, String description) {
        StringBuilder tagLib = new StringBuilder();
        for (Tag t : all) {
            tagLib.append(t.getName()).append("、");
        }
        String system = "你是租房房源标签助手，只能从给定标签库中挑选标签，严禁自创新标签。";
        String user = "标签库：" + tagLib + "\n房源标题：" + title + "\n房源描述：" + description
                + "\n请从标签库中挑选最合适的 1~5 个标签，只输出标签名，用中文逗号分隔，不要输出任何解释。";
        String answer = chat(system, user);
        List<String> names = new ArrayList<>();
        if (answer != null && !answer.isBlank()) {
            for (String part : answer.split("[,，、]")) {
                String n = part.trim();
                if (!n.isEmpty()) {
                    names.add(n);
                }
            }
        }
        return names;
    }

    /**
     * 通用对话调用（OpenAI 兼容的 /chat/completions 接口）
     *
     * @return 模型回复文本；调用失败返回空串
     */
    public String chat(String system, String user) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", aiProperties.getModel());
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", system));
            messages.add(Map.of("role", "user", "content", user));
            body.put("messages", messages);
            body.put("temperature", 0.3);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiProperties.getApiKey());
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            ResponseEntity<String> resp = restTemplate.postForEntity(
                    aiProperties.getBaseUrl() + "/chat/completions", entity, String.class);
            JsonNode root = objectMapper.readTree(resp.getBody());
            return root.path("choices").path(0).path("message").path("content").asText("");
        } catch (Exception e) {
            return "";
        }
    }
}
