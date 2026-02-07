package com.jinspace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.Transaction;
import com.jinspace.mapper.TransactionMapper;
import com.jinspace.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 交易记录服务实现类
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {
    @Override
    public TransactionMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    @Override
    public Map<String, Object> getTransactionStats(Integer userId, Date startDate, Date endDate) {
        // 构建查询条件
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        
        // 查询用户的所有交易记录
        List<Transaction> transactions = baseMapper.selectByMap(queryMap);
        
        // 过滤时间范围内的交易记录
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            boolean inDateRange = true;
            
            if (startDate != null && transaction.getTransactionDate().before(startDate)) {
                inDateRange = false;
            }
            
            if (endDate != null && transaction.getTransactionDate().after(endDate)) {
                inDateRange = false;
            }
            
            if (inDateRange) {
                filteredTransactions.add(transaction);
            }
        }
        
        // 计算总收入、总支出和结余
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction transaction : filteredTransactions) {
            if ("income".equals(transaction.getType())) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if ("expense".equals(transaction.getType())) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        // 构建返回结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncome", totalIncome);
        stats.put("totalExpense", totalExpense);
        stats.put("balance", balance);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getTransactionChartData(Integer userId, String timeRange, String type, String startDateStr, String endDateStr) {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        Date endDate = new Date();
        Date startDate = null;
        
        // 按日期分组并计算金额
        Map<String, Map<String, BigDecimal>> dateAmountMap = new LinkedHashMap<>();
        
        // 生成日期列表
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        // 根据时间范围计算开始日期和生成日期列表
        if ("week".equals(timeRange)) {
            // 本周：周一到周日
            Calendar weekStartCal = Calendar.getInstance();
            weekStartCal.setFirstDayOfWeek(Calendar.MONDAY); // 设置周一为一周的第一天
            weekStartCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 定位到本周一
            
            // 生成周一到周日的日期
            for (int i = 0; i < 7; i++) {
                Calendar tempCal = (Calendar) weekStartCal.clone();
                tempCal.add(Calendar.DAY_OF_MONTH, i);
                String dateStr = sdf.format(tempCal.getTime());
                dates.add(dateStr);
                
                // 初始化每个日期的收入和支出为0
                Map<String, BigDecimal> amountMap = new HashMap<>();
                amountMap.put("income", BigDecimal.ZERO);
                amountMap.put("expense", BigDecimal.ZERO);
                dateAmountMap.put(dateStr, amountMap);
            }
            
            startDate = weekStartCal.getTime();
            // 计算本周日
            Calendar weekEndCal = (Calendar) weekStartCal.clone();
            weekEndCal.add(Calendar.DAY_OF_MONTH, 6);
            endDate = weekEndCal.getTime();
        } else if ("month".equals(timeRange)) {
            // 本月：1日到月底
            Calendar monthStartCal = Calendar.getInstance();
            monthStartCal.set(Calendar.DAY_OF_MONTH, 1); // 本月1日
            
            Calendar monthEndCal = Calendar.getInstance();
            monthEndCal.add(Calendar.MONTH, 1);
            monthEndCal.set(Calendar.DAY_OF_MONTH, 0); // 本月最后一天
            
            // 生成1日到月底的日期
            int daysInMonth = monthEndCal.get(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= daysInMonth; i++) {
                Calendar tempCal = Calendar.getInstance();
                tempCal.set(Calendar.DAY_OF_MONTH, i);
                String dateStr = sdf.format(tempCal.getTime());
                dates.add(dateStr);
                
                // 初始化每个日期的收入和支出为0
                Map<String, BigDecimal> amountMap = new HashMap<>();
                amountMap.put("income", BigDecimal.ZERO);
                amountMap.put("expense", BigDecimal.ZERO);
                dateAmountMap.put(dateStr, amountMap);
            }
            
            startDate = monthStartCal.getTime();
            endDate = monthEndCal.getTime();
        } else if ("custom".equals(timeRange)) {
            // 自定义日期范围
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                startDate = dateFormat.parse(startDateStr);
                endDate = dateFormat.parse(endDateStr);
                
                // 生成自定义日期范围内的所有日期
                Calendar tempCal = Calendar.getInstance();
                tempCal.setTime(startDate);
                
                while (!tempCal.getTime().after(endDate)) {
                    String dateStr = sdf.format(tempCal.getTime());
                    dates.add(dateStr);
                    
                    // 初始化每个日期的收入和支出为0
                    Map<String, BigDecimal> amountMap = new HashMap<>();
                    amountMap.put("income", BigDecimal.ZERO);
                    amountMap.put("expense", BigDecimal.ZERO);
                    dateAmountMap.put(dateStr, amountMap);
                    
                    // 日期加1天
                    tempCal.add(Calendar.DAY_OF_MONTH, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 如果日期解析失败，返回空数据
                dates = new ArrayList<>();
                dateAmountMap = new LinkedHashMap<>();
            }
        }
        
        // 构建查询条件
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("user_id", userId);
        
        // 查询用户的所有交易记录
        List<Transaction> transactions = baseMapper.selectByMap(queryMap);
        
        // 过滤时间范围内的交易记录
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionDate().after(startDate) && 
                transaction.getTransactionDate().before(endDate) || 
                transaction.getTransactionDate().equals(startDate) || 
                transaction.getTransactionDate().equals(endDate)) {
                filteredTransactions.add(transaction);
            }
        }
        
        // 统计每天的收入和支出
        for (Transaction transaction : filteredTransactions) {
            String dateStr = sdf.format(transaction.getTransactionDate());
            if (dateAmountMap.containsKey(dateStr)) {
                Map<String, BigDecimal> amountMap = dateAmountMap.get(dateStr);
                if ("income".equals(transaction.getType())) {
                    amountMap.put("income", amountMap.get("income").add(transaction.getAmount()));
                } else if ("expense".equals(transaction.getType())) {
                    amountMap.put("expense", amountMap.get("expense").add(transaction.getAmount()));
                }
            }
        }
        
        // 构建收入和支出数据列表
        List<BigDecimal> incomeData = new ArrayList<>();
        List<BigDecimal> expenseData = new ArrayList<>();
        
        for (String date : dates) {
            Map<String, BigDecimal> amountMap = dateAmountMap.get(date);
            incomeData.add(amountMap.get("income"));
            expenseData.add(amountMap.get("expense"));
        }
        
        // 构建返回结果
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("dates", dates);
        chartData.put("incomeData", incomeData);
        chartData.put("expenseData", expenseData);
        
        return chartData;
    }
}
