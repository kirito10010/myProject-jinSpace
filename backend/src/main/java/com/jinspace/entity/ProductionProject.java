package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 生产项目实体类
 * 对应production_projects表
 */
@Data
@TableName("production_projects")
public class ProductionProject {
    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 项目编码，唯一
     */
    private String projectCode;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 项目负责人/创建人ID
     */
    private Integer uid;
    
    /**
     * 作业工序额定效率
     */
    private Integer workQuotaEfficiency;
    
    /**
     * 质检工序额定效率
     */
    private Integer inspectQuotaEfficiency;
    
    /**
     * 状态：1-启用，0-停用
     */
    private Integer status;
    
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