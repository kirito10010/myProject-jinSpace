package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 每日绩效记录实体类
 * 对应daily_performance表
 */
@Data
@TableName("daily_performance")
public class DailyPerformance {
    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer uid;
    
    /**
     * 绩效日期，格式：2026-01-24
     */
    private String date;
    
    /**
     * 生产项目
     */
    private String project;
    
    /**
     * 工序：作业、质检
     */
    private String process;
    
    /**
     * 额定效率
     */
    private Integer quotaEfficiency;
    
    /**
     * 实际工作量
     */
    private Integer actualWorkload;
    
    /**
     * 绩效人天合计
     */
    private BigDecimal performanceManDays;
    
    /**
     * 创建时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private Date createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}