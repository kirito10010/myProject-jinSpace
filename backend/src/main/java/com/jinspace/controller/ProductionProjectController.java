package com.jinspace.controller;

import com.jinspace.entity.ProductionProject;
import com.jinspace.service.ProductionProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生产项目控制器
 * 处理生产项目相关的HTTP请求
 */
@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProductionProjectController {
    
    @Autowired
    private ProductionProjectService productionProjectService;
    
    /**
     * 获取用户的所有生产项目
     * @param uid 用户ID
     * @return 生产项目列表
     */
    @GetMapping("/list")
    public Map<String, Object> getAllProjects(@RequestParam Integer uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<ProductionProject> projects = productionProjectService.getAllProjectsByUid(uid);
            result.put("code", 200);
            result.put("message", "获取生产项目列表成功");
            result.put("success", true);
            result.put("data", projects);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取生产项目列表失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 根据ID和用户ID获取生产项目
     * @param id 项目ID
     * @param uid 用户ID
     * @return 生产项目
     */
    @GetMapping("/{id}")
    public Map<String, Object> getProjectById(@PathVariable Integer id, @RequestParam Integer uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ProductionProject project = productionProjectService.getProjectByIdAndUid(id, uid);
            if (project != null) {
                result.put("code", 200);
                result.put("message", "获取生产项目成功");
                result.put("success", true);
                result.put("data", project);
            } else {
                result.put("code", 404);
                result.put("message", "生产项目不存在或无权限访问");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取生产项目失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 添加生产项目
     * @param project 生产项目
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public Map<String, Object> addProject(@RequestBody ProductionProject project) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 确保项目有uid字段
            if (project.getUid() == null) {
                result.put("code", 400);
                result.put("message", "用户ID不能为空");
                result.put("success", false);
                return result;
            }
            
            boolean success = productionProjectService.addProject(project);
            if (success) {
                result.put("code", 200);
                result.put("message", "添加生产项目成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "添加生产项目失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "添加生产项目失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 修改生产项目
     * @param project 生产项目
     * @param uid 用户ID
     * @return 是否修改成功
     */
    @PutMapping("/update")
    public Map<String, Object> updateProject(@RequestBody ProductionProject project, @RequestParam Integer uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = productionProjectService.updateProject(project, uid);
            if (success) {
                result.put("code", 200);
                result.put("message", "修改生产项目成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "修改生产项目失败，项目不存在或无权限访问");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "修改生产项目失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 删除生产项目
     * @param id 项目ID
     * @param uid 用户ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProject(@PathVariable Integer id, @RequestParam Integer uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = productionProjectService.deleteProject(id, uid);
            if (success) {
                result.put("code", 200);
                result.put("message", "删除生产项目成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "删除生产项目失败，项目不存在或无权限访问");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除生产项目失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 批量删除生产项目
     * @param request 请求体，包含项目ID列表和用户ID
     * @return 是否删除成功
     */
    @DeleteMapping("/batchDelete")
    public Map<String, Object> batchDeleteProjects(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Integer> ids = (List<Integer>) request.get("ids");
            Integer uid = (Integer) request.get("uid");
            
            if (ids == null || uid == null) {
                result.put("code", 400);
                result.put("message", "项目ID列表和用户ID不能为空");
                result.put("success", false);
                return result;
            }
            
            boolean success = productionProjectService.batchDeleteProjects(ids, uid);
            if (success) {
                result.put("code", 200);
                result.put("message", "批量删除生产项目成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "批量删除生产项目失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除生产项目失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
}