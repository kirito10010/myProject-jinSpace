package com.jinspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinspace.entity.SalaryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 薪资记录Mapper
 */
@Mapper
public interface SalaryRecordMapper extends BaseMapper<SalaryRecord> {

    /**
     * 根据用户ID和年月获取薪资记录
     * @param uid 用户ID
     * @param yearMonth 年月，如2024-01
     * @return 薪资记录
     */
    SalaryRecord selectByUidAndYearMonth(@Param("uid") String uid, @Param("yearMonth") String yearMonth);

}
