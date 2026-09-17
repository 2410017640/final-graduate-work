package com.smartrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.entity.House;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房源 Mapper（继承 MyBatis-Plus 的 BaseMapper，自带 CRUD）
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {
}
