package com.jinspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.SalaryRecord;
import com.jinspace.mapper.SalaryRecordMapper;
import com.jinspace.service.SalaryRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 薪资记录Service实现类
 */
@Service
public class SalaryRecordServiceImpl extends ServiceImpl<SalaryRecordMapper, SalaryRecord> implements SalaryRecordService {

    @Resource
    private SalaryRecordMapper salaryRecordMapper;

    @Override
    @Transactional
    public boolean saveSalaryRecord(SalaryRecord salaryRecord) {
        // 检查是否已存在该月份的薪资记录
        QueryWrapper<SalaryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`uid`", salaryRecord.getUid())
                .eq("`year_month`", salaryRecord.getYearMonth());
        SalaryRecord existingRecord = salaryRecordMapper.selectOne(queryWrapper);
        if (existingRecord != null) {
            // 更新已存在的记录
            existingRecord.setPeriodStart(salaryRecord.getPeriodStart());
            existingRecord.setPeriodEnd(salaryRecord.getPeriodEnd());
            existingRecord.setAttendanceDays(salaryRecord.getAttendanceDays());
            existingRecord.setBaseSalary(salaryRecord.getBaseSalary());
            existingRecord.setPerformance(salaryRecord.getPerformance());
            existingRecord.setPerformanceSalary(salaryRecord.getPerformanceSalary());
            existingRecord.setPositionPerformance(salaryRecord.getPositionPerformance());
            existingRecord.setMealAllowance(salaryRecord.getMealAllowance());
            existingRecord.setHousingAllowance(salaryRecord.getHousingAllowance());
            existingRecord.setFullAttendance(salaryRecord.getFullAttendance());
            existingRecord.setOtherBonus(salaryRecord.getOtherBonus());
            existingRecord.setPension(salaryRecord.getPension());
            existingRecord.setMedical(salaryRecord.getMedical());
            existingRecord.setUnemployment(salaryRecord.getUnemployment());
            existingRecord.setLateDeduction(salaryRecord.getLateDeduction());
            existingRecord.setOvertimeHours(salaryRecord.getOvertimeHours());
            existingRecord.setOvertimeSalary(salaryRecord.getOvertimeSalary());
            existingRecord.setTotalIncome(salaryRecord.getTotalIncome());
            existingRecord.setTotalDeduction(salaryRecord.getTotalDeduction());
            existingRecord.setNetSalary(salaryRecord.getNetSalary());
            return salaryRecordMapper.updateById(existingRecord) > 0;
        } else {
            // 插入新记录
            return salaryRecordMapper.insert(salaryRecord) > 0;
        }
    }

    @Override
    @Transactional
    public boolean updateSalaryRecord(SalaryRecord salaryRecord) {
        // 检查记录是否存在
        SalaryRecord existingRecord = salaryRecordMapper.selectById(salaryRecord.getId());
        if (existingRecord != null && existingRecord.getUid().equals(salaryRecord.getUid())) {
            return salaryRecordMapper.updateById(salaryRecord) > 0;
        }
        return false;
    }

    @Override
    public List<SalaryRecord> getSalaryRecordsByUid(String uid) {
        QueryWrapper<SalaryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("`uid`", uid);
        return salaryRecordMapper.selectList(queryWrapper);
    }

    @Override
    public SalaryRecord getSalaryRecordById(Integer id) {
        return salaryRecordMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean deleteSalaryRecord(Integer id, String uid) {
        // 检查记录是否存在且属于该用户
        SalaryRecord existingRecord = salaryRecordMapper.selectById(id);
        if (existingRecord != null && existingRecord.getUid().equals(uid)) {
            return salaryRecordMapper.deleteById(id) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean batchDeleteSalaryRecords(List<Integer> ids, String uid) {
        // 检查所有记录是否属于该用户
        for (Integer id : ids) {
            SalaryRecord existingRecord = salaryRecordMapper.selectById(id);
            if (existingRecord == null || !existingRecord.getUid().equals(uid)) {
                return false;
            }
        }
        return salaryRecordMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public SalaryRecordMapper getBaseMapper() {
        return salaryRecordMapper;
    }

}
