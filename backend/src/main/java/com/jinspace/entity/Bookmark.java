package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户网址书签实体类
 */
@Data
@TableName("user_bookmarks")
public class Bookmark {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long uid;
    
    /**
     * 网址
     */
    private String url;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;
}
