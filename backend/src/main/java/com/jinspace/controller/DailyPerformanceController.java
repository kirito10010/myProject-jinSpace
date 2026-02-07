package com.jinspace.controller;

import com.jinspace.entity.DailyPerformance;
import com.jinspace.service.DailyPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日绩效记录控制器
 * 处理每日绩效记录相关的HTTP请求
 */
@RestController
@RequestMapping("/api/performance")
@CrossOrigin
public class DailyPerformanceController {
    
    @Autowired
    private DailyPerformanceService dailyPerformanceService;
    
    /**
     * 保存每日绩效记录
     * @param performance 每日绩效记录
     * @return 是否保存成功
     */
    @PostMapping("/add")
    public Map<String, Object> addDailyPerformance(@RequestBody DailyPerformance performance) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 确保记录有uid字段
            if (performance.getUid() == null) {
                result.put("code", 400);
                result.put("message", "用户ID不能为空");
                result.put("success", false);
                return result;
            }
            
            boolean success = dailyPerformanceService.saveDailyPerformance(performance);
            if (success) {
                result.put("code", 200);
                result.put("message", "保存每日绩效记录成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "保存每日绩效记录失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "保存每日绩效记录失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 根据ID获取每日绩效记录
     * @param id 记录ID
     * @param uid 用户ID
     * @return 每日绩效记录
     */
    @GetMapping("/{id}")
    public Map<String, Object> getDailyPerformanceById(@PathVariable Integer id, @RequestParam Integer uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            DailyPerformance performance = dailyPerformanceService.getDailyPerformanceById(id, uid);
            if (performance != null) {
                result.put("code", 200);
                result.put("message", "获取每日绩效记录成功");
                result.put("success", true);
                result.put("data", performance);
            } else {
                result.put("code", 404);
                result.put("message", "每日绩效记录不存在或无权限访问");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取每日绩效记录失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 获取用户的每日绩效记录
     * @param uid 用户ID
     * @param date 日期（可选）
     * @return 每日绩效记录列表
     */
    @GetMapping("/list")
    public Map<String, Object> getDailyPerformances(@RequestParam Integer uid, @RequestParam(required = false) String date) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<DailyPerformance> performances;
            if (date != null) {
                // 根据用户ID和日期获取
                performances = dailyPerformanceService.getDailyPerformancesByUidAndDate(uid, date);
            } else {
                // 根据用户ID获取所有
                performances = dailyPerformanceService.getDailyPerformancesByUid(uid);
            }
            
            result.put("code", 200);
            result.put("message", "获取每日绩效记录成功");
            result.put("success", true);
            result.put("data", performances);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取每日绩效记录失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 获取用户指定日期范围内的每日绩效记录
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日绩效记录列表
     */
    @GetMapping("/range")
    public Map<String, Object> getDailyPerformancesByDateRange(
            @RequestParam Integer uid,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<DailyPerformance> performances = dailyPerformanceService.getDailyPerformancesByUidAndDateRange(uid, startDate, endDate);
            
            result.put("code", 200);
            result.put("message", "获取每日绩效记录成功");
            result.put("success", true);
            result.put("data", performances);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取每日绩效记录失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 删除每日绩效记录
     * @param id 记录ID
     * @param uid 用户ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteDailyPerformance(@PathVariable Integer id, @RequestParam Integer uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = dailyPerformanceService.deleteDailyPerformance(id, uid);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除每日绩效记录成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "删除每日绩效记录失败，记录不存在或无权限访问");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除每日绩效记录失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
}