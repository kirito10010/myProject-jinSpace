package com.jinspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinspace.entity.InstallerFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 安装程序文件Mapper接口
 * 继承BaseMapper，使用MyBatis Plus提供的CRUD方法
 */
@Mapper
public interface InstallerFileMapper extends BaseMapper<InstallerFile> {
    // 可以在这里添加自定义的查询方法
}
