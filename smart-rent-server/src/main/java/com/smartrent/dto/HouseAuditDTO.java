package com.smartrent.dto;

import lombok.Data;

/**
 * 管理员审核房源时接收的参数
 * pass=1 通过，pass=0 拒绝（拒绝时 rejectReason 必填）
 */
@Data
public class HouseAuditDTO {
    private Integer pass;
    private String rejectReason;
}
