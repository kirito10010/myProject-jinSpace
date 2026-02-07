package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 城市实体类
 */
@Data
@TableName("city")
public class City {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 行政区划代码
     */
    private Integer code;

    /**
     * 名称
     */
    private String name;

    /**
     * 上级id
     */
    private Long pid;

    /**
     * 类型：0-全国，1-省，2-市，3-区/县
     */
    private String type;
}
