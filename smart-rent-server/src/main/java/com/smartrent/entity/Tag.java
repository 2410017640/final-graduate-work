package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体，对应 tag 表（房源标签字典）
 */
@Data
@TableName("tag")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标签名称，如：近地铁、精装修 */
    private String name;

    /** 分组，如：交通/户型/租约/设施/人群，仅用于前端展示归类 */
    private String category;

    private LocalDateTime createTime;
}
