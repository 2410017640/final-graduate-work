package com.smartrent.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 房源-标签关系实体，对应 house_tag 表（多对多中间表）
 * 没有自增主键，用 (house_id, tag_id) 联合主键
 */
@Data
@TableName("house_tag")
public class HouseTag implements Serializable {

    /** 房源ID */
    private Long houseId;

    /** 标签ID */
    private Long tagId;
}
