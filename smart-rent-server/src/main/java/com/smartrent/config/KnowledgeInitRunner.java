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
            {"押金", "押金什么时候退？", "退租时房屋验收无违约、无损坏，一般在交房后3-7个工作日内退还，具体以合同为准。"},
            {"押金", "什么情况下会扣押金？", "房屋或物品损坏、拖欠租金水电费、提前违约退租等，可能按合同约定扣除相应押金。"},
            {"合同", "租房合同要注意什么？", "确认房东身份和产权、租期、租金及支付方式、押金退还条件、维修责任、违约金等关键条款。"},
            {"合同", "租房合同一般签多久？", "通常签1年，也有半年或短租，租期和续租条件以双方协商并写入合同为准。"},
            {"合同", "提前退租算违约吗？", "合同期内提前退租一般算违约，可能扣押金或支付违约金，具体看合同违约条款。"},
            {"退租", "退租需要提前多久通知？", "一般需提前30天书面通知房东，具体以合同约定为准。"},
            {"退租", "退租要走什么流程？", "提前通知房东→约时间交房→核对水电表底数→验收房屋→结清费用→退还押金。"},
            {"费用", "水电燃气费怎么算？", "通常租客自理，按实际用量缴费；入住时记录表底数，退租时结清。"},
            {"费用", "物业费由谁承担？", "整租一般按合同约定由房东或租客承担；合租通常房东承担，具体以合同为准。"},
            {"费用", "租房要交中介费吗？", "通过中介找房一般需支付半个月到一个月租金作为中介费，直租则无此费用。"},
            {"合租", "合租要注意什么？", "确认公共区域使用、费用分摊、作息习惯和室友情况，最好写入合同。"},
            {"宠物", "租房可以养宠物吗？", "需提前与房东确认，多数房源对宠物有限制，违反约定可能扣押金。"},
            {"入住", "入住前要检查什么？", "检查水电燃气表底数、家具家电完好情况、门窗锁具，拍照留证并记录在交接单。"},
            {"入住", "入住后东西坏了谁修？", "自然损坏一般由房东负责维修；人为损坏由租客负责，具体看合同维修责任条款。"},
            {"租金", "租金一般怎么付？", "常见押一付三、押一付一，按月或按季支付；支付方式和周期以合同为准。"},
            {"短租", "支持短租吗？", "部分房源支持短租（1-3个月），租金通常略高，需提前与房东确认。"},
            {"看房", "看房要注意什么？", "看采光通风、隔音、周边配套和通勤，确认家电家具是否齐全，可预约线下实地看房。"}
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
