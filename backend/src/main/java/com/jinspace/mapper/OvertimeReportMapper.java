package com.jinspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinspace.entity.OvertimeReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 加班小时汇报Mapper接口
 * 继承BaseMapper，提供基本的CRUD操作
 */
@Mapper
public interface OvertimeReportMapper extends BaseMapper<OvertimeReport> {
    
    /**
     * 根据用户ID获取加班记录列表
     * @param uid 用户ID
     * @return 加班记录列表
     */
    List<OvertimeReport> selectByUid(String uid);
    
    /**
     * 根据用户ID和日期获取加班记录
     * @param uid 用户ID
     * @param reportDate 汇报日期
     * @return 加班记录
     */
    OvertimeReport selectByUidAndReportDate(String uid, String reportDate);
    
    /**
     * 根据用户ID和日期范围获取加班记录列表
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 加班记录列表
     */
    List<OvertimeReport> selectByUidAndDateRange(String uid, String startDate, String endDate);
}
