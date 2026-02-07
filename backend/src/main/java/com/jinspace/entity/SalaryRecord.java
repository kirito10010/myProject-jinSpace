package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 月度薪资记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("salary_records")
public class SalaryRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 员工ID
     */
    @TableField("`uid`")
    private String uid;

    /**
     * 年月，如2024-01
     */
    @TableField("`year_month`")
    private String yearMonth;

    /**
     * 周期开始日期
     */
    @TableField("`period_start`")
    private Date periodStart;

    /**
     * 周期结束日期
     */
    @TableField("`period_end`")
    private Date periodEnd;

    /**
     * 出勤天数
     */
    @TableField("`attendance_days`")
    private Integer attendanceDays;

    /**
     * 基本薪资
     */
    @TableField("`base_salary`")
    private BigDecimal baseSalary;

    /**
     * 绩效系数/评分
     */
    @TableField("`performance`")
    private BigDecimal performance;

    /**
     * 绩效薪资
     */
    @TableField("`performance_salary`")
    private BigDecimal performanceSalary;

    /**
     * 岗位绩效
     */
    @TableField("`position_performance`")
    private BigDecimal positionPerformance;

    /**
     * 餐补
     */
    @TableField("`meal_allowance`")
    private BigDecimal mealAllowance;

    /**
     * 房补
     */
    @TableField("`housing_allowance`")
    private BigDecimal housingAllowance;

    /**
     * 全勤奖
     */
    @TableField("`full_attendance`")
    private BigDecimal fullAttendance;

    /**
     * 其他奖金
     */
    @TableField("`other_bonus`")
    private BigDecimal otherBonus;

    /**
     * 养老保险
     */
    @TableField("`pension`")
    private BigDecimal pension;

    /**
     * 医疗保险
     */
    @TableField("`medical`")
    private BigDecimal medical;

    /**
     * 失业保险
     */
    @TableField("`unemployment`")
    private BigDecimal unemployment;

    /**
     * 迟到扣款
     */
    @TableField("`late_deduction`")
    private BigDecimal lateDeduction;

    /**
     * 加班时长(小时)
     */
    @TableField("`overtime_hours`")
    private BigDecimal overtimeHours;

    /**
     * 加班薪资
     */
    @TableField("`overtime_salary`")
    private BigDecimal overtimeSalary;

    /**
     * 应发总额
     */
    @TableField("`total_income`")
    private BigDecimal totalIncome;

    /**
     * 扣除总额
     */
    @TableField("`total_deduction`")
    private BigDecimal totalDeduction;

    /**
     * 实发薪资
     */
    @TableField("`net_salary`")
    private BigDecimal netSalary;

    /**
     * 创建时间
     */
    @TableField("`created_at`")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("`updated_at`")
    private Date updatedAt;

}
