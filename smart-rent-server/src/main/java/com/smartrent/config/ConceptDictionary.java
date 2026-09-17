package com.smartrent.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 语义概念词典：把近义/相关的词归为同一"概念"，用于非预制标签的语义匹配。
 * 例：湖、池塘、湖泊 都归为"水景"，租客搜"有湖"也能匹配到描述含"池塘"的房源。
 */
@Component
public class ConceptDictionary {

    /** 概念 -> 相关词列表 */
    private final Map<String, List<String>> concepts = new LinkedHashMap<>();

    /** 词 -> 所属概念（反向索引） */
    private final Map<String, String> wordToConcept = new LinkedHashMap<>();

    public ConceptDictionary() {
        add("水景", "湖", "湖泊", "池塘", "水塘", "湖景", "水景", "海景", "河景", "人工湖");
        add("钓鱼", "钓鱼", "垂钓", "鱼塘", "钓鱼台", "钓场");
        add("交通", "地铁", "公交", "车站", "轨道交通", "交通枢纽", "高铁站", "火车站");
        add("购物", "超市", "商场", "便利店", "购物中心", "商圈", "菜市场", "步行街");
        add("健身", "健身房", "游泳馆", "游泳池", "跑道", "体育馆", "球场", "瑜伽馆");
        add("宠物", "养宠物", "宠物", "遛狗", "宠物医院");
    }

    private void add(String concept, String... words) {
        concepts.put(concept, Arrays.asList(words));
        for (String w : words) {
            wordToConcept.put(w, concept);
        }
    }

    /** 词所属的概念，未收录返回 null */
    public String conceptOf(String word) {
        return wordToConcept.get(word);
    }

    /** 概念下的所有相关词 */
    public List<String> wordsOf(String concept) {
        return concepts.get(concept);
    }

    /** 词典收录的全部词 */
    public Set<String> allWords() {
        return wordToConcept.keySet();
    }
}
