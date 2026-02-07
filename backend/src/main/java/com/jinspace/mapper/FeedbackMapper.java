package com.jinspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinspace.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈Mapper接口
 * 继承BaseMapper，使用MyBatis Plus提供的CRUD方法
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
    
}
