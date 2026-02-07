package com.jinspace.controller;

import com.jinspace.entity.Transaction;
import com.jinspace.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易记录控制器
 * 处理记账相关请求
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * 添加交易记录
     * @param transaction 交易记录信息
     * @return 添加结果
     */
    @PostMapping
    public Map<String, Object> addTransaction(@RequestBody Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用前端传递的userId
            boolean success = transactionService.save(transaction);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "记账成功");
                result.put("success", true);
                result.put("data", transaction);
            } else {
                result.put("code", 400);
                result.put("message", "记账失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "记账失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 获取交易记录列表
     * @param params 查询参数
     * @return 交易记录列表
     */
    @GetMapping
    public Map<String, Object> getTransactions(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从请求参数中获取userId
            Integer userId = Integer.parseInt(params.getOrDefault("userId", "1").toString());
            
            // 构建查询条件
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("user_id", userId);
            
            // 添加分类筛选
            if (params.containsKey("category") && params.get("category") != null && !params.get("category").toString().isEmpty()) {
                queryMap.put("category", params.get("category"));
            }
            
            // 获取交易记录列表
            List<Transaction> transactions = transactionService.listByMap(queryMap);
            
            // 添加日期范围筛选
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (params.containsKey("startDate") && params.get("startDate") != null && !params.get("startDate").toString().isEmpty() &&
                params.containsKey("endDate") && params.get("endDate") != null && !params.get("endDate").toString().isEmpty()) {
                Date startDate = sdf.parse(params.get("startDate").toString());
                Date endDate = sdf.parse(params.get("endDate").toString());
                
                // 过滤日期范围内的记录
                List<Transaction> filteredTransactions = new ArrayList<>();
                for (Transaction transaction : transactions) {
                    if (transaction.getTransactionDate().after(startDate) && transaction.getTransactionDate().before(endDate) ||
                        transaction.getTransactionDate().equals(startDate) || transaction.getTransactionDate().equals(endDate)) {
                        filteredTransactions.add(transaction);
                    }
                }
                transactions = filteredTransactions;
            }
            
            // 添加排序
            String sort = (String) params.get("sort");
            String order = (String) params.get("order");
            if (sort != null && !sort.isEmpty() && order != null && !order.isEmpty()) {
                // 根据排序字段和顺序排序
                Collections.sort(transactions, (a, b) -> {
                    int cmp = 0;
                    
                    // 比较排序字段
                    if ("transactionDate".equals(sort)) {
                        cmp = a.getTransactionDate().compareTo(b.getTransactionDate());
                    } else if ("createdAt".equals(sort)) {
                        cmp = a.getCreatedAt().compareTo(b.getCreatedAt());
                    }
                    
                    // 根据排序顺序调整比较结果
                    return "asc".equals(order) ? cmp : -cmp;
                });
            } else {
                // 默认按创建时间从新到旧排序
                Collections.sort(transactions, (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            }
            
            // 处理分页
            int current = params.containsKey("page") ? Integer.parseInt(params.get("page").toString()) : 1;
            int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            // 计算分页起始和结束索引
            int startIndex = (current - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, transactions.size());
            
            // 获取分页数据
            List<Transaction> pageData = transactions.subList(startIndex, endIndex);
            
            // 构建分页响应
            Map<String, Object> pageResult = new HashMap<>();
            pageResult.put("records", pageData);
            pageResult.put("total", transactions.size());
            pageResult.put("size", pageSize);
            pageResult.put("current", current);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("success", true);
            result.put("data", pageResult);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 获取交易统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Map<String, Object> getTransactionStats(@RequestParam(required = false) String startDate, 
                                                  @RequestParam(required = false) String endDate,
                                                  @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 解析日期字符串为Date对象
            Date start = null;
            Date end = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            if (startDate != null && !startDate.isEmpty()) {
                start = sdf.parse(startDate);
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                end = sdf.parse(endDate);
                // 设置结束日期为当天的23:59:59
                end.setHours(23);
                end.setMinutes(59);
                end.setSeconds(59);
            }
            
            // 获取统计数据
            Map<String, Object> stats = transactionService.getTransactionStats(userId, start, end);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 更新交易记录
     * @param id 交易记录ID
     * @param transaction 交易记录信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateTransaction(@PathVariable Integer id, @RequestBody Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            transaction.setId(id);
            boolean success = transactionService.updateById(transaction);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "更新成功");
                result.put("success", true);
                result.put("data", transaction);
            } else {
                result.put("code", 400);
                result.put("message", "更新失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 删除交易记录
     * @param id 交易记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTransaction(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = transactionService.removeById(id);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "删除成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "删除失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 批量删除交易记录
     * @param ids 交易记录ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Map<String, Object> batchDeleteTransactions(@RequestBody List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (ids == null || ids.isEmpty()) {
                result.put("code", 400);
                result.put("message", "请选择要删除的记录");
                result.put("success", false);
                return result;
            }
            
            boolean success = transactionService.removeByIds(ids);
            
            if (success) {
                result.put("code", 200);
                result.put("message", "批量删除成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "批量删除失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 获取交易图表数据
     * @param timeRange 时间范围（week或month或custom）
     * @param type 类型（income、expense或both）
     * @param userId 用户ID
     * @param startDate 开始日期（custom时必填）
     * @param endDate 结束日期（custom时必填）
     * @return 图表数据
     */
    @GetMapping("/chart")
    public Map<String, Object> getChartData(@RequestParam String timeRange, 
                                           @RequestParam String type, 
                                           @RequestParam Integer userId,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> chartData = transactionService.getTransactionChartData(userId, timeRange, type, startDate, endDate);
            
            result.put("code", 200);
            result.put("message", "获取图表数据成功");
            result.put("success", true);
            result.put("data", chartData);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取图表数据失败，服务器错误：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
}
