package com.smartrent.controller;

import com.smartrent.common.LoginUserContext;
import com.smartrent.common.RequireRole;
import com.smartrent.common.Result;
import com.smartrent.common.Role;
import com.smartrent.dto.AppointmentCreateDTO;
import com.smartrent.entity.Appointment;
import com.smartrent.service.AppointmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 预约看房接口
 *
 * 权限说明：
 * - 预约：仅 TENANT
 * - 我的预约：登录即可
 * - 收到的预约 / 确认：仅 LANDLORD
 * - 取消：登录即可（服务层校验是否本人）
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /** 预约看房（租客） */
    @PostMapping
    @RequireRole(Role.TENANT)
    public Result<Long> create(@RequestBody AppointmentCreateDTO dto) {
        return Result.success(appointmentService.create(LoginUserContext.getUserId(), dto));
    }

    /** 我的预约 */
    @GetMapping("/my")
    public Result<List<Appointment>> my() {
        return Result.success(appointmentService.my(LoginUserContext.getUserId()));
    }

    /** 房东收到的预约 */
    @GetMapping("/received")
    @RequireRole(Role.LANDLORD)
    public Result<List<Appointment>> received() {
        return Result.success(appointmentService.received(LoginUserContext.getUserId()));
    }

    /** 房东确认预约 */
    @PostMapping("/{id}/confirm")
    @RequireRole(Role.LANDLORD)
    public Result<Void> confirm(@PathVariable Long id) {
        appointmentService.confirm(id, LoginUserContext.getUserId());
        return Result.success();
    }

    /** 取消预约（租客或房东） */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        appointmentService.cancel(id, LoginUserContext.getUserId());
        return Result.success();
    }
}
