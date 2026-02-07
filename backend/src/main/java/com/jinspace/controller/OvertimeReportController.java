package com.jinspace.controller;

import com.jinspace.common.Result;
import com.jinspace.entity.OvertimeReport;
import com.jinspace.service.OvertimeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 加班小时汇报Controller
 */
@RestController
@RequestMapping("/api/overtime")
public class OvertimeReportController {
    
    @Autowired
    private OvertimeReportService overtimeReportService;
    
    /**
     * 添加加班记录
     * @param overtimeReport 加班记录
     * @return Result
     */
    @PostMapping("/add")
    public Result addOvertimeReport(@RequestBody OvertimeReport overtimeReport) {
        try {
            boolean result = overtimeReportService.saveOvertimeReport(overtimeReport);
            if (result) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的加班记录列表
     * @param uid 用户ID
     * @return Result
     */
    @GetMapping("/list")
    public Result getOvertimeReports(@RequestParam String uid) {
        try {
            List<OvertimeReport> overtimeReports = overtimeReportService.getOvertimeReportsByUid(uid);
            return Result.success(overtimeReports);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取加班记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据日期范围获取用户的加班记录列表
     * @param uid 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Result
     */
    @GetMapping("/listByDateRange")
    public Result getOvertimeReportsByDateRange(@RequestParam String uid, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            List<OvertimeReport> overtimeReports = overtimeReportService.getOvertimeReportsByUidAndDateRange(uid, startDate, endDate);
            return Result.success(overtimeReports);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取加班记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新加班记录
     * @param overtimeReport 加班记录
     * @return Result
     */
    @PutMapping("/update")
    public Result updateOvertimeReport(@RequestBody OvertimeReport overtimeReport) {
        try {
            boolean result = overtimeReportService.updateOvertimeReport(overtimeReport);
            if (result) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除加班记录
     * @param id 记录ID
     * @param uid 用户ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result deleteOvertimeReport(@PathVariable Integer id, @RequestParam String uid) {
        try {
            // 验证用户权限
            OvertimeReport overtimeReport = overtimeReportService.getOvertimeReportById(id);
            if (overtimeReport == null) {
                return Result.error("加班记录不存在");
            }
            if (!uid.equals(overtimeReport.getUid())) {
                return Result.error("无权删除该加班记录");
            }
            
            boolean result = overtimeReportService.deleteOvertimeReport(id);
            if (result) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除加班记录
     * @param ids 记录ID列表
     * @param uid 用户ID
     * @return Result
     */
    @DeleteMapping("/batch")
    public Result batchDeleteOvertimeReports(@RequestBody BatchDeleteRequest request) {
        try {
            List<Integer> ids = request.getIds();
            String uid = request.getUid();
            
            // 验证用户权限
            for (Integer id : ids) {
                OvertimeReport overtimeReport = overtimeReportService.getOvertimeReportById(id);
                if (overtimeReport == null) {
                    return Result.error("加班记录不存在");
                }
                if (!uid.equals(overtimeReport.getUid())) {
                    return Result.error("无权删除该加班记录");
                }
            }
            
            boolean result = overtimeReportService.batchDeleteOvertimeReports(ids);
            if (result) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除请求参数类
     */
    static class BatchDeleteRequest {
        private List<Integer> ids;
        private String uid;
        
        // getter and setter
        public List<Integer> getIds() {
            return ids;
        }
        
        public void setIds(List<Integer> ids) {
            this.ids = ids;
        }
        
        public String getUid() {
            return uid;
        }
        
        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
