package com.jinspace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易记录表实体类
 */
@Data
@TableName("transactions")
public class Transaction {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private BigDecimal amount;
    
    private String type;
    
    private String category;
    
    private String description;
    
    private Date transactionDate;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
