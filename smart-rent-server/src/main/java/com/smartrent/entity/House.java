package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 房源实体，对应 house 表
 */
@Data
@TableName("house")
public class House {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布房东ID（关联 user.id） */
    private Long landlordId;

    private String title;
    private String description;
    private String address;

    /** 面积(㎡) */
    private BigDecimal area;

    /** 室数 / 厅数 */
    private Integer roomCount;
    private Integer hallCount;

    /** 月租(元) */
    private BigDecimal rent;

    /** 租赁方式 1整租 2合租 */
    private Integer rentType;

    private String orientation;
    private Integer floor;
    private Integer totalFloor;

    /** 图片URL，逗号分隔（暂未做文件上传，先存地址） */
    private String images;

    /**
     * 审核状态：
     * 0 待审核 / 1 已通过(上架) / 2 已拒绝 / 3 已下架
     */
    private Integer status;

    /** 审核拒绝原因 */
    private String rejectReason;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 房源标签列表（非数据库字段，查询时由 HouseService 回填，方便前端直接展示）
     */
    @TableField(exist = false)
    private List<Tag> tags;

    // ===== 状态常量，避免到处写魔法数字 =====
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_OFFLINE = 3;
}
