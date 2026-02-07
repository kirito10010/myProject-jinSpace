package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 安装程序文件实体类
 * 对应数据库表 installer_files
 */
@Data
@TableName("installer_files")
public class InstallerFile {
    
    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 上传用户ID
     */
    @TableField("user_id")
    private Integer userId;
    
    /**
     * 原始文件名
     */
    @TableField("file_name")
    private String fileName;
    
    /**
     * 存储路径
     */
    @TableField("file_path")
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;
    
    /**
     * 文件哈希（用于去重）
     */
    @TableField("file_hash")
    private String fileHash;
    
    /**
     * 上传时间
     */
    @TableField("upload_time")
    private Date uploadTime;
    
    /**
     * 文件状态：uploaded, verified, active, archived
     */
    @TableField("status")
    private String status;
    
    /**
     * 程序版本号
     */
    @TableField("version")
    private String version;
    
    /**
     * 目标平台：windows, linux, macos, android, ios
     */
    @TableField("platform")
    private String platform;
    
    /**
     * 文件描述
     */
    @TableField("description")
    private String description;
}
