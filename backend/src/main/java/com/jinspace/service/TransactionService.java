package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 交易记录服务接口
 */
public interface TransactionService extends IService<Transaction> {
    
    /**
     * 获取交易统计数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getTransactionStats(Integer userId, Date startDate, Date endDate);
    
    /**
     * 获取交易图表数据
     * @param userId 用户ID
     * @param timeRange 时间范围（week或month或custom）
     * @param type 类型（income、expense或both）
     * @param startDate 开始日期（custom时必填）
     * @param endDate 结束日期（custom时必填）
     * @return 图表数据
     */
    Map<String, Object> getTransactionChartData(Integer userId, String timeRange, String type, String startDate, String endDate);
}
