package com.smartrent.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.entity.Knowledge;
import com.smartrent.mapper.KnowledgeMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 首次启动时播种一批租房常见知识（仅当 knowledge 表为空时执行一次）。
 * 这些知识供 P8 AI 问答、P9 RAG 使用。
 */
@Component
public class KnowledgeInitRunner implements CommandLineRunner {

    private final KnowledgeMapper knowledgeMapper;

    public KnowledgeInitRunner(KnowledgeMapper knowledgeMapper) {
        this.knowledgeMapper = knowledgeMapper;
    }

    /** 预置知识：分类 -> 问题 -> 答案 */
    private static final String[][] SEED = {
            {"押金", "租房押金一般是多少？", "一般为1-2个月租金，具体看合同约定；退租时无违约、无损坏应全额退还。"},
            {"合同", "租房合同要注意什么？", "确认房东身份和产权、租期、租金及支付方式、押金退还条件、维修责任、违约金等关键条款。"},
            {"退租", "退租需要提前多久通知？", "一般需提前30天书面通知房东，具体以合同约定为准。"},
            {"费用", "水电燃气费怎么算？", "通常租客自理，按实际用量缴费；入住时记录表底数，退租时结清。"},
            {"合租", "合租要注意什么？", "确认公共区域使用、费用分摊、作息习惯和室友情况，最好写入合同。"},
            {"宠物", "租房可以养宠物吗？", "需提前与房东确认，多数房源对宠物有限制，违反约定可能扣押金。"}
    };

    @Override
    public void run(String... args) {
        Long count = knowledgeMapper.selectCount(new LambdaQueryWrapper<Knowledge>());
        if (count != null && count > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (String[] row : SEED) {
            Knowledge k = new Knowledge();
            k.setCategory(row[0]);
            k.setQuestion(row[1]);
            k.setAnswer(row[2]);
            k.setCreateTime(now);
            k.setUpdateTime(now);
            knowledgeMapper.insert(k);
        }
        System.out.println("[SmartRent] 已播种租房知识库 " + SEED.length + " 条");
    }
}
