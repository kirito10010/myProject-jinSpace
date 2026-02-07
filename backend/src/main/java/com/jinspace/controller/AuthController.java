package com.jinspace.controller;

import com.jinspace.entity.User;
import com.jinspace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理登录、注册等请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册接口
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = userService.register(user);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "注册成功");
                result.put("success", true);
                
                // 返回用户信息（不包含密码）
                User userInfo = userService.getUserByUsername(user.getUsername());
                if (userInfo != null) {
                    userInfo.setPassword(null); // 不返回密码
                }
                result.put("data", userInfo);
            } else {
                result.put("code", 400);
                result.put("message", "注册失败，用户名或邮箱已存在");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 用户登录接口
     * @param loginData 登录数据（包含usernameOrEmail和password）
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String usernameOrEmail = loginData.get("usernameOrEmail");
            String password = loginData.get("password");
            
            // 参数验证
            if (usernameOrEmail == null || usernameOrEmail.isEmpty() || password == null || password.isEmpty()) {
                result.put("code", 400);
                result.put("message", "用户名/邮箱和密码不能为空");
                result.put("success", false);
                return result;
            }
            
            // 调用登录服务
            User user = userService.login(usernameOrEmail, password);
            
            if (user != null) {
                result.put("code", 200);
                result.put("message", "登录成功");
                result.put("success", true);
                result.put("data", user);
            } else {
                result.put("code", 401);
                result.put("message", "登录失败，用户名/邮箱或密码错误");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
}
