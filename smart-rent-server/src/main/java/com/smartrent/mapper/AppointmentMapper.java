package com.smartrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
