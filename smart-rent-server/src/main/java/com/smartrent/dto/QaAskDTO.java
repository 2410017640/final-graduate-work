package com.smartrent.dto;

import lombok.Data;

/**
 * 租客提问时接收的参数
 */
@Data
public class QaAskDTO {
    private String question;
    /** 关联房源ID（可空，用于定位需通知的房东） */
    private Long houseId;
}
