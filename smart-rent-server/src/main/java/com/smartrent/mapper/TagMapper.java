package com.smartrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签 Mapper（继承 MyBatis-Plus 的 BaseMapper，自带 CRUD）
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
