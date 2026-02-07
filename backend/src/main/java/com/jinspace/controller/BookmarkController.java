package com.jinspace.controller;

import com.jinspace.entity.Bookmark;
import com.jinspace.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户网址书签控制器
 * 处理获取书签列表、添加书签、修改书签、删除书签等请求
 */
@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin
public class BookmarkController {
    
    @Autowired
    private BookmarkService bookmarkService;
    
    /**
     * 获取用户网址列表（分页）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping
    public Map<String, Object> getBookmarks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            // 注意：这里简化处理，实际项目中应该从认证信息中获取用户ID
            @RequestParam(defaultValue = "1") Long uid) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用服务获取书签列表
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Bookmark> bookmarkPage = bookmarkService.getBookmarksByUid(uid, page, pageSize);
            
            // 构建返回结果
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "获取书签列表成功");
            result.put("total", bookmarkPage.getTotal());
            result.put("list", bookmarkPage.getRecords());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "获取书签列表失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 添加新的网址书签
     * @param bookmark 书签信息
     * @return 添加结果
     */
    @PostMapping
    public Map<String, Object> addBookmark(@RequestBody Bookmark bookmark) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 注意：这里简化处理，实际项目中应该从认证信息中获取用户ID
            // 临时使用固定的用户ID=1
            bookmark.setUid(1L);
            
            // 调用服务添加书签
            boolean success = bookmarkService.addBookmark(bookmark);
            
            if (success) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "添加书签成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "添加书签失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "添加书签失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 修改网址书签
     * @param id 书签ID
     * @param bookmark 书签信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 设置书签ID
            bookmark.setId(id);
            
            // 注意：这里简化处理，实际项目中应该从认证信息中获取用户ID
            // 临时使用固定的用户ID=1
            bookmark.setUid(1L);
            
            // 调用服务修改书签
            boolean success = bookmarkService.updateBookmark(bookmark);
            
            if (success) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "修改书签成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "修改书签失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "修改书签失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 删除单个网址书签
     * @param id 书签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteBookmark(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用服务删除书签
            boolean success = bookmarkService.deleteBookmarkById(id);
            
            if (success) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "删除书签成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "删除书签失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "删除书签失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量删除网址书签
     * @param ids 书签ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Map<String, Object> batchDeleteBookmarks(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用服务批量删除书签
            boolean success = bookmarkService.batchDeleteBookmarks(ids);
            
            if (success) {
                result.put("code", 200);
                result.put("success", true);
                result.put("message", "批量删除书签成功");
            } else {
                result.put("code", 400);
                result.put("success", false);
                result.put("message", "批量删除书签失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "批量删除书签失败：" + e.getMessage());
        }
        
        return result;
    }
}
