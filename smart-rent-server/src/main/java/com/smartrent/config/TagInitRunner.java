package com.smartrent.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.entity.Tag;
import com.smartrent.mapper.TagMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 系统启动时播种一批预置标签（仅当 tag 表为空时执行一次）
 *
 * 这样 P4 一上线就有可用的标签体系，房东发布房源时可以直接勾选；
 * 后续 P5（AI 生成标签）也可以从这些预置标签里挑选合适的给房源打上。
 */
@Component
public class TagInitRunner implements CommandLineRunner {

    private final TagMapper tagMapper;

    public TagInitRunner(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /** 预置标签：分组 -> 标签名列表 */
    private static final String[][] SEED = {
            {"交通", "近地铁,近公交,近学校,近商圈"},
            {"户型", "一室一厅,两室一厅,大开间,带独卫,主卧带卫"},
            {"租约", "可短租,押一付一,可月付,免中介费,可续租"},
            {"设施", "精装修,家电齐全,有电梯,可做饭,有阳台,有停车位,独立空调,宽带入户"},
            {"人群", "可养宠,仅限女生,仅限男生,合租,整租优先"}
    };

    @Override
    public void run(String... args) {
        Long count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>());
        if (count != null && count > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (String[] group : SEED) {
            String category = group[0];
            for (String name : group[1].split(",")) {
                Tag tag = new Tag();
                tag.setName(name.trim());
                tag.setCategory(category);
                tag.setCreateTime(now);
                tagMapper.insert(tag);
            }
        }
        System.out.println("[SmartRent] 已播种预置标签：" + SEED.length + " 个分组");
    }
}
