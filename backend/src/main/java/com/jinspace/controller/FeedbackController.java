package com.jinspace.controller;

import com.jinspace.common.Result;
import com.jinspace.entity.Feedback;
import com.jinspace.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 反馈控制器
 * 处理反馈相关请求
 */
@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    /**
     * 提交反馈接口
     * @param feedback 反馈信息
     * @return 提交结果
     */
    @PostMapping
    public Result<?> submitFeedback(@RequestBody Feedback feedback) {
        try {
            boolean success = feedbackService.submitFeedback(feedback);
            
            if (success) {
                return Result.success("反馈提交成功");
            } else {
                return Result.error(400, "反馈提交失败");
            }
        } catch (Exception e) {
            return Result.error(500, "反馈提交失败，服务器错误：" + e.getMessage());
        }
    }
    
    /**
     * 获取反馈列表接口（管理员）
     * @param params 查询参数
     * @return 反馈列表
     */
    @GetMapping
    public Result<?> getFeedbackList(@RequestParam Map<String, Object> params) {
        try {
            // 这里可以根据需要添加分页和条件查询逻辑
            return Result.success(feedbackService.list());
        } catch (Exception e) {
            return Result.error(500, "获取反馈列表失败，服务器错误：" + e.getMessage());
        }
    }
    
    /**
     * 更新反馈接口（管理员）
     * @param id 反馈ID
     * @param feedback 反馈信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        try {
            Feedback existingFeedback = feedbackService.getById(id);
            if (existingFeedback == null) {
                return Result.error(404, "反馈不存在");
            }
            
            feedback.setId(id);
            boolean success = feedbackService.updateById(feedback);
            
            if (success) {
                return Result.success("反馈更新成功");
            } else {
                return Result.error(400, "反馈更新失败");
            }
        } catch (Exception e) {
            return Result.error(500, "反馈更新失败，服务器错误：" + e.getMessage());
        }
    }
    
    /**
     * 更新反馈状态接口（管理员）
     * @param id 反馈ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result<?> updateFeedbackStatus(@PathVariable Long id, @RequestBody Map<String, Integer> status) {
        try {
            Feedback feedback = feedbackService.getById(id);
            if (feedback == null) {
                return Result.error(404, "反馈不存在");
            }
            
            feedback.setStatus(status.get("status"));
            boolean success = feedbackService.updateById(feedback);
            
            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.error(400, "状态更新失败");
            }
        } catch (Exception e) {
            return Result.error(500, "状态更新失败，服务器错误：" + e.getMessage());
        }
    }
    
    /**
     * 删除反馈接口（管理员）
     * @param id 反馈ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFeedback(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.getById(id);
            if (feedback == null) {
                return Result.error(404, "反馈不存在");
            }
            
            boolean success = feedbackService.removeById(id);
            
            if (success) {
                return Result.success("反馈删除成功");
            } else {
                return Result.error(400, "反馈删除失败");
            }
        } catch (Exception e) {
            return Result.error(500, "反馈删除失败，服务器错误：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除反馈接口（管理员）
     * @param ids 反馈ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<?> batchDeleteFeedback(@RequestBody List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error(400, "请选择要删除的反馈");
            }
            
            boolean success = feedbackService.removeByIds(ids);
            
            if (success) {
                return Result.success("批量删除反馈成功");
            } else {
                return Result.error(400, "批量删除反馈失败");
            }
        } catch (Exception e) {
            return Result.error(500, "批量删除反馈失败，服务器错误：" + e.getMessage());
        }
    }
}
