package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问答记录实体，对应 question 表
 */
@Data
@TableName("question")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租客提问内容 */
    private String question;

    /** 回答内容（知识库自动 或 房东手动） */
    private String answer;

    /** 关联房源ID（可空） */
    private Long houseId;

    /** 需通知/回答的房东ID */
    private Long landlordId;

    /** 提问的租客ID */
    private Long askerId;

    /** 状态：0待回答 1已回答 */
    private Integer status;

    /** 是否知识库自动回答：1是 0否 */
    private Integer answeredByKb;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ANSWERED = 1;
}
