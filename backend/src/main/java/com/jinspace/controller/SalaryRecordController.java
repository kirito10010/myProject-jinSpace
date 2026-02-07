package com.jinspace.controller;

import com.jinspace.common.Result;
import com.jinspace.entity.SalaryRecord;
import com.jinspace.service.SalaryRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 薪资记录Controller
 */
@RestController
@RequestMapping("/api/salary")
public class SalaryRecordController {

    @Resource
    private SalaryRecordService salaryRecordService;

    /**
     * 添加工资记录
     * @param salaryRecord 工资记录
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<?> addSalaryRecord(@RequestBody SalaryRecord salaryRecord) {
        try {
            boolean result = salaryRecordService.saveSalaryRecord(salaryRecord);
            if (result) {
                return Result.success("工资记录添加成功");
            } else {
                return Result.error("工资记录添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加工资记录失败：" + e.getMessage());
        }
    }

    /**
     * 修改工资记录
     * @param salaryRecord 工资记录
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<?> updateSalaryRecord(@RequestBody SalaryRecord salaryRecord) {
        try {
            boolean result = salaryRecordService.updateSalaryRecord(salaryRecord);
            if (result) {
                return Result.success("工资记录修改成功");
            } else {
                return Result.error("工资记录修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改工资记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取工资记录列表
     * @param uid 用户ID
     * @return 工资记录列表
     */
    @GetMapping("/list")
    public Result<?> getSalaryRecords(@RequestParam String uid) {
        try {
            List<SalaryRecord> salaryRecords = salaryRecordService.getSalaryRecordsByUid(uid);
            return Result.success(salaryRecords);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取工资记录列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除工资记录
     * @param id 工资记录ID
     * @param uid 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteSalaryRecord(@PathVariable Integer id, @RequestParam String uid) {
        try {
            boolean result = salaryRecordService.deleteSalaryRecord(id, uid);
            if (result) {
                return Result.success("工资记录删除成功");
            } else {
                return Result.error("工资记录删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除工资记录失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除工资记录
     * @param request 批量删除请求
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<?> batchDeleteSalaryRecords(@RequestBody BatchDeleteRequest request) {
        try {
            boolean result = salaryRecordService.batchDeleteSalaryRecords(request.getIds(), request.getUid());
            if (result) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量删除工资记录失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除请求
     */
    public static class BatchDeleteRequest {
        private List<Integer> ids;
        private String uid;

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
