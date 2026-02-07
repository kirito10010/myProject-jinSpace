package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 * 对应user表
 */
@Data
@TableName("user")
public class User {
    /**
     * 用户唯一ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 登录账号（唯一）
     */
    private String username;
    
    /**
     * 加密后的密码（如MD5/SHA256）
     */
    private String password;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 邮箱（唯一，用于找回密码）
     */
    private String email;
    
    /**
     * 用户权限：1-超级管理员，2-管理员，3-普通成员
     */
    private Integer role;
    
    /**
     * 账号状态：1-正常，0-禁用
     */
    private Integer status;
    
    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
}
