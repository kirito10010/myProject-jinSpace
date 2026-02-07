package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.OvertimeReport;

import java.util.List;

/**
 * 加班小时汇报Service接口
 */
public interface OvertimeReportService extends IService<OvertimeReport> {
    
    /**
     * 保存加班记录
     * @param overtimeReport 加班记录
     * @return 保存结果
     */
    boolean saveOvertimeReport(OvertimeReport overtimeReport);
    
    /**
     * 根据用户ID获取加班记录列表
     * @param uid 用户ID
     * @return 加班记录列表
     */
    List<OvertimeReport> getOvertimeReportsByUid(String uid);
    
    /**
     * 根据ID获取加班记录
     * @param id 记录ID
     * @return 加班记录
     */
    OvertimeReport getOvertimeReportById(Integer id);
    
    /**
     * 更新加班记录
     * @param overtimeReport 加班记录
     * @return 更新结果
     */
    boolean updateOvertimeReport(OvertimeReport overtimeReport);
    
    /**
     * 删除加班记录
     * @param id 记录ID
     * @return 删除结果
     */
    boolean deleteOvertimeReport(Integer id);
    
    /**
     * 批量删除加班记录
     * @param ids 记录ID列表
     * @return 删除结果
     */
    boolean batchDeleteOvertimeReports(List<Integer> ids);
    
    /**
     * 根据用户ID和日期范围获取加班记录列表
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 加班记录列表
     */
    List<OvertimeReport> getOvertimeReportsByUidAndDateRange(String uid, String startDate, String endDate);
}
