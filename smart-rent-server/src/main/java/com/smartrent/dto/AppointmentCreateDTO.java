package com.smartrent.dto;

import lombok.Data;

/**
 * 租客预约看房时接收的参数
 */
@Data
public class AppointmentCreateDTO {
    private Long houseId;
    private String appointmentTime;
    private String message;
}
