package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.SalaryRecord;

import java.util.List;

/**
 * 薪资记录Service
 */
public interface SalaryRecordService extends IService<SalaryRecord> {

    /**
     * 保存薪资记录
     * @param salaryRecord 薪资记录
     * @return 保存结果
     */
    boolean saveSalaryRecord(SalaryRecord salaryRecord);

    /**
     * 更新薪资记录
     * @param salaryRecord 薪资记录
     * @return 更新结果
     */
    boolean updateSalaryRecord(SalaryRecord salaryRecord);

    /**
     * 根据用户ID获取薪资记录列表
     * @param uid 用户ID
     * @return 薪资记录列表
     */
    List<SalaryRecord> getSalaryRecordsByUid(String uid);

    /**
     * 根据ID获取薪资记录
     * @param id 薪资记录ID
     * @return 薪资记录
     */
    SalaryRecord getSalaryRecordById(Integer id);

    /**
     * 删除薪资记录
     * @param id 薪资记录ID
     * @param uid 用户ID
     * @return 删除结果
     */
    boolean deleteSalaryRecord(Integer id, String uid);

    /**
     * 批量删除薪资记录
     * @param ids 薪资记录ID列表
     * @param uid 用户ID
     * @return 删除结果
     */
    boolean batchDeleteSalaryRecords(List<Integer> ids, String uid);

}
