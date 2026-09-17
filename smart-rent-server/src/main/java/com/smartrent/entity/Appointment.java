package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约看房实体，对应 appointment 表
 */
@Data
@TableName("appointment")
public class Appointment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long houseId;
    private Long tenantId;
    private Long landlordId;

    /** 预约看房时间（字符串，如 "2026-09-20 14:00"） */
    private String appointmentTime;

    private String message;

    /** 0待确认 1已确认 2已取消 3已完成 */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_CANCELED = 2;
    public static final int STATUS_DONE = 3;
}
