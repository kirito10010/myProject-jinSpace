package com.jinspace.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.User;
import com.jinspace.mapper.UserMapper;
import com.jinspace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户服务实现类
 * 实现注册、登录等业务逻辑
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public UserMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public boolean register(User user) {
        // 1. 检查用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            return false;
        }
        
        // 2. 检查邮箱是否已存在
        if (getUserByEmail(user.getEmail()) != null) {
            return false;
        }
        
        // 3. 使用MD5加密密码
        String encryptedPassword = SecureUtil.md5(user.getPassword());
        user.setPassword(encryptedPassword);
        
        // 4. 设置默认值
        if (user.getRole() == null) {
            user.setRole(3); // 3-普通成员
        }
        if (user.getStatus() == null) {
            user.setStatus(1); // 1-正常
        }
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername()); // 默认使用用户名作为昵称
        }
        
        // 5. 插入用户
        return save(user);
    }
    
    @Override
    public User login(String usernameOrEmail, String password) {
        // 1. 根据用户名或邮箱查询用户
        User user = userMapper.getUserByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        
        // 2. 检查用户是否存在
        if (user == null) {
            return null;
        }
        
        // 3. 检查用户状态是否正常
        if (user.getStatus() != 1) {
            return null;
        }
        
        // 4. 验证密码（将输入密码MD5加密后与数据库中的密码比较）
        String encryptedPassword = SecureUtil.md5(password);
        if (!user.getPassword().equals(encryptedPassword)) {
            return null;
        }
        
        // 5. 更新最后登录时间
        updateLastLoginTime(user.getId());
        
        // 6. 返回用户信息（注意：不要返回密码）
        user.setPassword(null);
        return user;
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }
    
    @Override
    public boolean updateLastLoginTime(Long id) {
        User user = new User();
        user.setId(id);
        user.setLastLoginTime(new Date());
        return updateById(user);
    }
}
