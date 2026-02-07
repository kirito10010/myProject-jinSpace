package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 反馈实体类
 * 对应feedback表
 */
@Data
@TableName("feedback")
public class Feedback {
    /**
     * 反馈ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long uid;
    
    /**
     * 反馈内容
     */
    private String content;
    
    /**
     * 联系方式
     */
    private String contact;
    
    /**
     * 反馈类型：1-建议，2-问题，3-投诉，4-其他
     */
    private Integer feedbackType;
    
    /**
     * 状态：1-待处理，2-处理中，3-已处理，4-已关闭
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
