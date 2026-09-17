package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.dto.FilterCondition;
import com.smartrent.entity.Tag;
import com.smartrent.mapper.TagMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 规则版自然语言解析器（无需大模型，按关键词/正则解析）
 *
 * 支持解析：
 * - 室数："两居室"/"两室一厅"/"2室"
 * - 租金："4000以内"、"4000以上"、"预算4000"、"3000-5000"
 * - 面积："60平米"/"60平"/"60㎡"
 * - 标签：句子中包含的标签名（近地铁、精装修等）
 */
@Component
public class RuleNlParser implements NlParser {

    private final TagMapper tagMapper;

    public RuleNlParser(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /** 中文数字 -> 阿拉伯数字 */
    private static final Map<Character, Integer> CN_NUM = new HashMap<>();

    static {
        CN_NUM.put('一', 1);
        CN_NUM.put('二', 2);
        CN_NUM.put('两', 2);
        CN_NUM.put('三', 3);
        CN_NUM.put('四', 4);
        CN_NUM.put('五', 5);
        CN_NUM.put('六', 6);
        CN_NUM.put('七', 7);
        CN_NUM.put('八', 8);
        CN_NUM.put('九', 9);
        CN_NUM.put('十', 10);
    }

    @Override
    public FilterCondition parse(String query) {
        FilterCondition fc = new FilterCondition();
        if (query == null || query.isBlank()) {
            return fc;
        }
        String q = query.trim();

        parseRent(q, fc);
        fc.setRoomCount(parseRoom(q));
        fc.setMinArea(parseArea(q));
        fc.setTagIds(matchTags(q));

        // 没有任何结构化条件命中时，把整句作为关键词兜底（走标题/地址模糊搜索）
        if (isEmpty(fc.getTagIds()) && fc.getRoomCount() == null
                && fc.getMinRent() == null && fc.getMaxRent() == null
                && fc.getMinArea() == null) {
            fc.setKeyword(q);
        }
        return fc;
    }

    /** 解析租金：以内/以下 -> 上限，以上 -> 下限，预算X -> 上限，X-Y -> 区间 */
    private void parseRent(String q, FilterCondition fc) {
        Matcher m = Pattern.compile("(\\d+)\\s*元?(?:以内|以下|上下|左右|内)").matcher(q);
        if (m.find()) {
            fc.setMaxRent(new BigDecimal(m.group(1)));
            return;
        }
        m = Pattern.compile("(\\d+)\\s*元?(?:以上|起)").matcher(q);
        if (m.find()) {
            fc.setMinRent(new BigDecimal(m.group(1)));
            return;
        }
        m = Pattern.compile("预算\\s*(\\d+)").matcher(q);
        if (m.find()) {
            fc.setMaxRent(new BigDecimal(m.group(1)));
            return;
        }
        m = Pattern.compile("(\\d+)\\s*[-~到至]\\s*(\\d+)").matcher(q);
        if (m.find()) {
            fc.setMinRent(new BigDecimal(m.group(1)));
            fc.setMaxRent(new BigDecimal(m.group(2)));
        }
    }

    /** 解析室数："两居室"/"两室一厅"/"2室" */
    private Integer parseRoom(String q) {
        Matcher m = Pattern.compile("([一二两三四五六七八九十])(?:居|室)").matcher(q);
        if (m.find()) {
            return CN_NUM.get(m.group(1).charAt(0));
        }
        m = Pattern.compile("(\\d)\\s*(?:居|室)").matcher(q);
        if (m.find()) {
            return Integer.valueOf(m.group(1));
        }
        return null;
    }

    /** 解析面积："60平米"/"60平"/"60㎡" */
    private BigDecimal parseArea(String q) {
        Matcher m = Pattern.compile("(\\d+)\\s*(?:平米|平方米|平|㎡)").matcher(q);
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return null;
    }

    /** 匹配句子中包含的标签名 */
    private List<Long> matchTags(String q) {
        List<Tag> all = tagMapper.selectList(new LambdaQueryWrapper<Tag>());
        List<Long> ids = new ArrayList<>();
        for (Tag t : all) {
            if (t.getName() != null && q.contains(t.getName())) {
                ids.add(t.getId());
            }
        }
        return ids;
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
