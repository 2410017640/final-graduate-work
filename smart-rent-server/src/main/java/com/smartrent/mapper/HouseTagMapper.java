package com.smartrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.entity.HouseTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房源-标签关系 Mapper（继承 MyBatis-Plus 的 BaseMapper，自带 CRUD）
 */
@Mapper
public interface HouseTagMapper extends BaseMapper<HouseTag> {
}
