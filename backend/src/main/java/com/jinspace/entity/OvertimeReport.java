package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 加班小时汇报实体类
 * 对应overtime_report表
 */
@Data
@TableName("overtime_report")
public class OvertimeReport {
    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private String uid;
    
    /**
     * 汇报日期，格式：2026-01-24
     */
    private String reportDate;
    
    /**
     * 加班小时数（支持0.5的倍数）
     */
    private BigDecimal overtimeHours;
    
    /**
     * 项目名称（可选）
     */
    private String projectName;
    
    /**
     * 加班描述
     */
    private String description;
    
    /**
     * 状态：pending（待审批）、approved（已通过）、rejected（已拒绝）
     */
    private String status;
    
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
