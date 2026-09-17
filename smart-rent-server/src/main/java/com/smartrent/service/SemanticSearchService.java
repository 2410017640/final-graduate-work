package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.config.ConceptDictionary;
import com.smartrent.dto.SemanticMatchVO;
import com.smartrent.entity.House;
import com.smartrent.entity.Tag;
import com.smartrent.mapper.HouseMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 语义检索服务：租客用自然语言（可能含非预制特征，如"有湖"）检索房源，
 * 通过"概念词典(近义词) + 轻量向量化(字符二元组余弦相似度)"匹配描述语义相近的房源。
 *
 * 说明：真实系统会用 embedding 模型把描述转成向量并建索引；本项目"先不用 key"，
 * 用字符二元组向量 + 概念词典替代，效果可控、可解释，后续可无缝换成真实 embedding。
 */
@Service
public class SemanticSearchService {

    private static final double COS_THRESHOLD = 0.35;

    private final HouseMapper houseMapper;
    private final TagService tagService;
    private final ConceptDictionary dictionary;

    public SemanticSearchService(HouseMapper houseMapper, TagService tagService,
                                 ConceptDictionary dictionary) {
        this.houseMapper = houseMapper;
        this.tagService = tagService;
        this.dictionary = dictionary;
    }

    public List<SemanticMatchVO> search(String query) {
        List<SemanticMatchVO> result = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return result;
        }
        List<House> houses = houseMapper.selectList(
                new LambdaQueryWrapper<House>().eq(House::getStatus, House.STATUS_APPROVED));
        Map<Long, List<Tag>> tagMap = tagService.listTagsByHouseIds(
                houses.stream().map(House::getId).toList());

        // query 中命中的词典词 + 所属概念
        Set<String> queryConcepts = new HashSet<>();
        for (String w : dictionary.allWords()) {
            if (query.contains(w)) {
                queryConcepts.add(dictionary.conceptOf(w));
            }
        }

        for (House house : houses) {
            List<Tag> tags = tagMap.getOrDefault(house.getId(), List.of());
            String text = buildText(house, tags);

            // 房屋文本里命中的词典词 -> 概念
            Map<String, String> houseHit = new HashMap<>();
            for (String w : dictionary.allWords()) {
                if (text.contains(w)) {
                    houseHit.put(w, dictionary.conceptOf(w));
                }
            }

            // 概念匹配：房屋命中的概念与 query 概念有交集（湖 -> 池塘 之类近义）
            Set<String> matched = new LinkedHashSet<>();
            for (Map.Entry<String, String> e : houseHit.entrySet()) {
                if (queryConcepts.contains(e.getValue())) {
                    matched.add(e.getKey());
                }
            }

            double cos = cosine(query, text);
            if (matched.isEmpty() && cos < COS_THRESHOLD) {
                continue;
            }

            SemanticMatchVO vo = new SemanticMatchVO();
            vo.setHouse(house);
            vo.setMatchedFeatures(new ArrayList<>(matched));

            // 是否含非预置标签命中的特征
            Set<String> presetNames = new HashSet<>();
            for (Tag t : tags) {
                presetNames.add(t.getName());
            }
            boolean nonPreset = matched.stream().anyMatch(w -> !presetNames.contains(w));
            vo.setNonPresetMatched(nonPreset);
            vo.setScore(cos);
            result.add(vo);
        }
        result.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return result;
    }

    private String buildText(House h, List<Tag> tags) {
        StringBuilder sb = new StringBuilder();
        sb.append(nz(h.getTitle())).append(' ')
          .append(nz(h.getDescription())).append(' ')
          .append(nz(h.getAddress()));
        for (Tag t : tags) {
            sb.append(' ').append(t.getName());
        }
        return sb.toString();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    /** 字符二元组向量 + 余弦相似度（轻量向量化检索） */
    private double cosine(String a, String b) {
        Map<String, Integer> va = bigrams(a);
        Map<String, Integer> vb = bigrams(b);
        double dot = 0, na = 0, nb = 0;
        for (Map.Entry<String, Integer> e : va.entrySet()) {
            na += (double) e.getValue() * e.getValue();
            Integer cb = vb.get(e.getKey());
            if (cb != null) {
                dot += (double) e.getValue() * cb;
            }
        }
        for (int c : vb.values()) {
            nb += (double) c * c;
        }
        if (na == 0 || nb == 0) {
            return 0;
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    private Map<String, Integer> bigrams(String s) {
        Map<String, Integer> m = new HashMap<>();
        String t = s == null ? "" : s;
        for (int i = 0; i + 1 < t.length(); i++) {
            String g = t.substring(i, i + 2);
            m.merge(g, 1, Integer::sum);
        }
        return m;
    }
}
