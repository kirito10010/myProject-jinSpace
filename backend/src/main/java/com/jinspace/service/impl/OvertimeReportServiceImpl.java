package com.jinspace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.OvertimeReport;
import com.jinspace.mapper.OvertimeReportMapper;
import com.jinspace.service.OvertimeReportService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 加班小时汇报Service实现类
 */
@Service
public class OvertimeReportServiceImpl extends ServiceImpl<OvertimeReportMapper, OvertimeReport> implements OvertimeReportService {
    
    @Override
    public OvertimeReportMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    /**
     * 保存加班记录
     * @param overtimeReport 加班记录
     * @return 保存结果
     */
    @Override
    public boolean saveOvertimeReport(OvertimeReport overtimeReport) {
        // 设置默认状态为已通过
        if (overtimeReport.getStatus() == null) {
            overtimeReport.setStatus("approved");
        }
        return save(overtimeReport);
    }
    
    /**
     * 根据用户ID获取加班记录列表
     * @param uid 用户ID
     * @return 加班记录列表
     */
    @Override
    public List<OvertimeReport> getOvertimeReportsByUid(String uid) {
        return baseMapper.selectByUid(uid);
    }
    
    /**
     * 根据ID获取加班记录
     * @param id 记录ID
     * @return 加班记录
     */
    @Override
    public OvertimeReport getOvertimeReportById(Integer id) {
        return getById(id);
    }
    
    /**
     * 更新加班记录
     * @param overtimeReport 加班记录
     * @return 更新结果
     */
    @Override
    public boolean updateOvertimeReport(OvertimeReport overtimeReport) {
        return updateById(overtimeReport);
    }
    
    /**
     * 删除加班记录
     * @param id 记录ID
     * @return 删除结果
     */
    @Override
    public boolean deleteOvertimeReport(Integer id) {
        return removeById(id);
    }
    
    /**
     * 批量删除加班记录
     * @param ids 记录ID列表
     * @return 删除结果
     */
    @Override
    public boolean batchDeleteOvertimeReports(List<Integer> ids) {
        return removeByIds(ids);
    }
    
    /**
     * 根据用户ID和日期范围获取加班记录列表
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 加班记录列表
     */
    @Override
    public List<OvertimeReport> getOvertimeReportsByUidAndDateRange(String uid, String startDate, String endDate) {
        return getBaseMapper().selectByUidAndDateRange(uid, startDate, endDate);
    }
}
