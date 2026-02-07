package com.jinspace.service;

import com.jinspace.entity.DailyPerformance;

import java.util.List;

/**
 * 每日绩效记录服务接口
 * 定义每日绩效记录的业务逻辑方法
 */
public interface DailyPerformanceService {
    
    /**
     * 保存每日绩效记录
     * @param performance 每日绩效记录
     * @return 是否保存成功
     */
    boolean saveDailyPerformance(DailyPerformance performance);
    
    /**
     * 根据ID获取每日绩效记录
     * @param id 记录ID
     * @param uid 用户ID
     * @return 每日绩效记录
     */
    DailyPerformance getDailyPerformanceById(Integer id, Integer uid);
    
    /**
     * 根据用户ID获取每日绩效记录
     * @param uid 用户ID
     * @return 每日绩效记录列表
     */
    List<DailyPerformance> getDailyPerformancesByUid(Integer uid);
    
    /**
     * 根据用户ID和日期获取每日绩效记录
     * @param uid 用户ID
     * @param date 日期
     * @return 每日绩效记录列表
     */
    List<DailyPerformance> getDailyPerformancesByUidAndDate(Integer uid, String date);
    
    /**
     * 根据用户ID和日期范围获取每日绩效记录
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日绩效记录列表
     */
    List<DailyPerformance> getDailyPerformancesByUidAndDateRange(Integer uid, String startDate, String endDate);
    
    /**
     * 删除每日绩效记录
     * @param id 记录ID
     * @param uid 用户ID
     * @return 是否删除成功
     */
    boolean deleteDailyPerformance(Integer id, Integer uid);
}