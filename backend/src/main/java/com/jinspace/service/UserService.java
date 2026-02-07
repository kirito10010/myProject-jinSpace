package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.User;
import com.jinspace.mapper.UserMapper;

/**
 * 用户服务接口
 * 定义注册、登录等业务方法
 */
public interface UserService extends IService<User> {
    /**
     * 获取基础Mapper
     * @return UserMapper
     */
    UserMapper getBaseMapper();
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果（true成功，false失败）
     */
    boolean register(User user);
    
    /**
     * 用户登录
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码（明文）
     * @return 登录成功返回用户信息，失败返回null
     */
    User login(String usernameOrEmail, String password);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱获取用户
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);
    
    /**
     * 更新用户最后登录时间
     * @param id 用户ID
     * @return 更新结果（true成功，false失败）
     */
    boolean updateLastLoginTime(Long id);
}
