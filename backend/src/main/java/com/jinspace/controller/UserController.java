package com.jinspace.controller;

import com.jinspace.entity.User;
import com.jinspace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.hutool.crypto.SecureUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 处理用户列表、更新、删除等请求
 */
@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userService.list();
            // 清除密码信息
            users.forEach(user -> user.setPassword(null));
            result.put("code", 200);
            result.put("data", users);
            result.put("success", true);
            result.put("message", "获取用户列表成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "获取用户列表失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查用户是否存在
            User existingUser = userService.getById(id);
            if (existingUser == null) {
                result.put("code", 404);
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            // 用户名唯一性验证
            if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
                User userByUsername = userService.getUserByUsername(user.getUsername());
                if (userByUsername != null) {
                    result.put("code", 400);
                    result.put("success", false);
                    result.put("message", "用户名已存在");
                    return result;
                }
            }

            // 邮箱唯一性验证
            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                User userByEmail = userService.getUserByEmail(user.getEmail());
                if (userByEmail != null) {
                    result.put("code", 400);
                    result.put("success", false);
                    result.put("message", "邮箱已存在");
                    return result;
                }
            }

            // 设置用户ID
            user.setId(id);

            // 如果密码不为空，则加密更新
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                // 使用hutool的MD5加密
                user.setPassword(SecureUtil.md5(user.getPassword()));
            } else {
                // 密码为空则不更新
                user.setPassword(existingUser.getPassword());
            }

            // 更新用户
            boolean success = userService.updateById(user);
            if (success) {
                // 返回更新后的用户信息（清除密码）
                User updatedUser = userService.getById(id);
                updatedUser.setPassword(null);
                result.put("code", 200);
                result.put("data", updatedUser);
                result.put("success", true);
                result.put("message", "更新用户成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "更新用户失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "更新用户失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除单个用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查用户是否存在
            if (userService.getById(id) == null) {
                result.put("code", 404);
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            // 删除用户
            boolean success = userService.removeById(id);
            if (success) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "删除用户成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "删除用户失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "删除用户失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Map<String, Object> batchDeleteUsers(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (ids == null || ids.isEmpty()) {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "请选择要删除的用户");
                return result;
            }

            // 批量删除用户
            boolean success = userService.removeByIds(ids);
            if (success) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "批量删除用户成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "批量删除用户失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "批量删除用户失败：" + e.getMessage());
        }
        return result;
    }
}
