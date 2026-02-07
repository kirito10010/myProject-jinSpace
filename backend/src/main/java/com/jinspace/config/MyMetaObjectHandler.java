package com.jinspace.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis Plus 元对象处理器
 * 自动填充创建时间和更新时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充新增记录的 createTime、createdTime、createdAt、updateTime 等字段
        Date now = new Date();
        setFieldValByName("createTime", now, metaObject);
        setFieldValByName("createdTime", now, metaObject);
        setFieldValByName("createdAt", now, metaObject);
        setFieldValByName("updateTime", now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 自动填充更新记录的 updateTime 等字段
        Date now = new Date();
        setFieldValByName("updateTime", now, metaObject);
        // 确保更新时也能处理createdTime和createdAt字段（如果需要）
        if (metaObject.hasGetter("createdTime") && metaObject.getValue("createdTime") == null) {
            setFieldValByName("createdTime", now, metaObject);
        }
        if (metaObject.hasGetter("createdAt") && metaObject.getValue("createdAt") == null) {
            setFieldValByName("createdAt", now, metaObject);
        }
    }
}