package com.jinspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.DailyPerformance;
import com.jinspace.mapper.DailyPerformanceMapper;
import com.jinspace.service.DailyPerformanceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 每日绩效记录服务实现类
 * 实现DailyPerformanceService接口，提供具体的业务逻辑
 */
@Service
public class DailyPerformanceServiceImpl extends ServiceImpl<DailyPerformanceMapper, DailyPerformance> implements DailyPerformanceService {
    
    @Override
    public boolean saveDailyPerformance(DailyPerformance performance) {
        // 检查是否已存在相同的绩效记录
        QueryWrapper<DailyPerformance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", performance.getUid())
                   .eq("date", performance.getDate())
                   .eq("project", performance.getProject())
                   .eq("process", performance.getProcess());
        
        DailyPerformance existingPerformance = getOne(queryWrapper);
        if (existingPerformance != null) {
            // 如果已存在，更新记录
            existingPerformance.setQuotaEfficiency(performance.getQuotaEfficiency());
            existingPerformance.setActualWorkload(performance.getActualWorkload());
            existingPerformance.setPerformanceManDays(performance.getPerformanceManDays());
            return updateById(existingPerformance);
        } else {
            // 如果不存在，添加新记录
            return save(performance);
        }
    }
    
    @Override
    public DailyPerformance getDailyPerformanceById(Integer id, Integer uid) {
        QueryWrapper<DailyPerformance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                   .eq("uid", uid);
        return getOne(queryWrapper);
    }
    
    @Override
    public List<DailyPerformance> getDailyPerformancesByUid(Integer uid) {
        QueryWrapper<DailyPerformance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid)
                   .orderByDesc("date");
        return list(queryWrapper);
    }
    
    @Override
    public List<DailyPerformance> getDailyPerformancesByUidAndDate(Integer uid, String date) {
        QueryWrapper<DailyPerformance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid)
                   .eq("date", date)
                   .orderByDesc("project")
                   .orderByDesc("process");
        return list(queryWrapper);
    }
    
    @Override
    public List<DailyPerformance> getDailyPerformancesByUidAndDateRange(Integer uid, String startDate, String endDate) {
        QueryWrapper<DailyPerformance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid)
                   .between("date", startDate, endDate)
                   .orderByDesc("date")
                   .orderByDesc("project")
                   .orderByDesc("process");
        return list(queryWrapper);
    }
    
    @Override
    public boolean deleteDailyPerformance(Integer id, Integer uid) {
        QueryWrapper<DailyPerformance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                   .eq("uid", uid);
        return remove(queryWrapper);
    }
    
    @Override
    public DailyPerformanceMapper getBaseMapper() {
        return super.getBaseMapper();
    }
}