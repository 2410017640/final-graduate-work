package com.smartrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
