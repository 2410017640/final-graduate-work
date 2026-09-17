package com.smartrent.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布/修改房源时接收的参数
 */
@Data
public class HousePublishDTO {
    private String title;
    private String description;
    private String address;
    private BigDecimal area;
    private Integer roomCount;
    private Integer hallCount;
    private BigDecimal rent;
    private Integer rentType;
    private String orientation;
    private Integer floor;
    private Integer totalFloor;
    private String images;
}
