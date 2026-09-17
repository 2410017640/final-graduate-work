package com.smartrent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.common.BusinessException;
import com.smartrent.dto.AppointmentCreateDTO;
import com.smartrent.entity.Appointment;
import com.smartrent.entity.House;
import com.smartrent.mapper.AppointmentMapper;
import com.smartrent.mapper.HouseMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约看房业务：租客预约 / 我的预约 / 房东收到的预约 / 确认 / 取消
 */
@Service
public class AppointmentService {

    private final AppointmentMapper appointmentMapper;
    private final HouseMapper houseMapper;

    public AppointmentService(AppointmentMapper appointmentMapper, HouseMapper houseMapper) {
        this.appointmentMapper = appointmentMapper;
        this.houseMapper = houseMapper;
    }

    /** 租客预约看房（房源须已上架） */
    public Long create(Long tenantId, AppointmentCreateDTO dto) {
        if (dto.getHouseId() == null) {
            throw new BusinessException("请选择房源");
        }
        House house = houseMapper.selectById(dto.getHouseId());
        if (house == null || house.getStatus() != House.STATUS_APPROVED) {
            throw new BusinessException("房源不存在或未上架");
        }
        Appointment a = new Appointment();
        a.setHouseId(dto.getHouseId());
        a.setTenantId(tenantId);
        a.setLandlordId(house.getLandlordId());
        a.setAppointmentTime(dto.getAppointmentTime());
        a.setMessage(dto.getMessage());
        a.setStatus(Appointment.STATUS_PENDING);
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(LocalDateTime.now());
        appointmentMapper.insert(a);
        return a.getId();
    }

    /** 我的预约（租客视角） */
    public List<Appointment> my(Long tenantId) {
        return appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getTenantId, tenantId)
                .orderByDesc(Appointment::getCreateTime));
    }

    /** 收到的预约（房东视角） */
    public List<Appointment> received(Long landlordId) {
        return appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getLandlordId, landlordId)
                .orderByDesc(Appointment::getCreateTime));
    }

    /** 房东确认预约 */
    public void confirm(Long appointmentId, Long landlordId) {
        Appointment a = appointmentMapper.selectById(appointmentId);
        if (a == null) {
            throw new BusinessException("预约不存在");
        }
        if (!landlordId.equals(a.getLandlordId())) {
            throw new BusinessException("只能确认分配给自己的预约");
        }
        if (a.getStatus() != Appointment.STATUS_PENDING) {
            throw new BusinessException("该预约已处理");
        }
        a.setStatus(Appointment.STATUS_CONFIRMED);
        a.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(a);
    }

    /** 取消预约（租客或房东均可） */
    public void cancel(Long appointmentId, Long userId) {
        Appointment a = appointmentMapper.selectById(appointmentId);
        if (a == null) {
            throw new BusinessException("预约不存在");
        }
        if (!userId.equals(a.getTenantId()) && !userId.equals(a.getLandlordId())) {
            throw new BusinessException("只能取消自己的预约");
        }
        a.setStatus(Appointment.STATUS_CANCELED);
        a.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(a);
    }
}
