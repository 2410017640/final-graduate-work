package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库条目实体，对应 knowledge 表（租房常见问题）
 */
@Data
@TableName("knowledge")
public class Knowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 问题/标题 */
    private String question;

    /** 答案/内容 */
    private String answer;

    /** 分类，如：押金/合同/退租/费用/合租/宠物 */
    private String category;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
