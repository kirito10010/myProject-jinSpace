package com.jinspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinspace.entity.DailyPerformance;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 每日绩效记录Mapper接口
 * 继承BaseMapper，提供基本的CRUD操作
 */
@Mapper
public interface DailyPerformanceMapper extends BaseMapper<DailyPerformance> {
    
    /**
     * 根据用户ID和日期获取每日绩效记录
     * @param uid 用户ID
     * @param date 日期
     * @return 每日绩效记录列表
     */
    List<DailyPerformance> selectByUidAndDate(Integer uid, String date);
    
    /**
     * 根据用户ID和日期范围获取每日绩效记录
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日绩效记录列表
     */
    List<DailyPerformance> selectByUidAndDateRange(Integer uid, String startDate, String endDate);
    
    /**
     * 根据用户ID、项目和工序获取每日绩效记录
     * @param uid 用户ID
     * @param project 项目
     * @param process 工序
     * @return 每日绩效记录列表
     */
    List<DailyPerformance> selectByUidAndProjectAndProcess(Integer uid, String project, String process);
}