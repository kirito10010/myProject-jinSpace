package com.jinspace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinspace.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 * 继承BaseMapper，使用MyBatis Plus提供的CRUD方法
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(@Param("email") String email);
    
    /**
     * 根据用户名或邮箱查询用户
     * 用于登录验证
     * @param username 用户名
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}
