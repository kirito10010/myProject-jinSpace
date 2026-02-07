package com.jinspace.controller;

import com.jinspace.entity.InstallerFile;
import com.jinspace.service.InstallerFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安装程序文件Controller
 * 处理安装程序文件相关的HTTP请求
 */
@RestController
@RequestMapping("/api/installer-files")
@CrossOrigin
public class InstallerFileController {
    
    @Autowired
    private InstallerFileService installerFileService;
    
    // 文件存储根路径
    private static final String STORAGE_PATH = "C:\\JinSpace\\ResourceExe";
    
    /**
     * 上传安装程序文件
     * @param file 上传的文件
     * @param userId 用户ID
     * @param version 程序版本号
     * @param platform 目标平台
     * @param description 文件描述
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Integer userId,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String description) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "文件不能为空");
                result.put("success", false);
                return result;
            }
            
            // 创建存储目录（如果不存在）
            File storageDir = new File(STORAGE_PATH);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            
            // 生成文件哈希值
            String fileHash = calculateFileHash(file);
            
            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            String uniqueFileName = System.currentTimeMillis() + "_" + fileHash + "." + extension;
            
            // 保存文件到本地
            String filePath = STORAGE_PATH + File.separator + uniqueFileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            
            // 保存文件信息到数据库
            InstallerFile installerFile = new InstallerFile();
            installerFile.setUserId(userId);
            installerFile.setFileName(originalFileName);
            installerFile.setFilePath(filePath);
            installerFile.setFileSize(file.getSize());
            installerFile.setFileHash(fileHash);
            installerFile.setUploadTime(new Date());
            installerFile.setStatus("uploaded");
            installerFile.setVersion(version);
            installerFile.setPlatform(platform);
            installerFile.setDescription(description);
            
            boolean saved = installerFileService.saveFile(installerFile);
            
            if (saved) {
                result.put("code", 200);
                result.put("message", "文件上传成功");
                result.put("success", true);
                result.put("data", installerFile);
            } else {
                result.put("code", 500);
                result.put("message", "文件信息保存失败");
                result.put("success", false);
                // 删除已上传的文件
                dest.delete();
            }
            
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "文件上传失败：" + e.getMessage());
            result.put("success", false);
        } catch (NoSuchAlgorithmException e) {
            result.put("code", 500);
            result.put("message", "文件哈希计算失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 获取用户上传的文件列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 文件列表
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserFiles(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用分页查询方法
            Page<InstallerFile> filePage = installerFileService.getFilesByUserId(userId, page, pageSize);
            result.put("code", 200);
            result.put("message", "获取文件列表成功");
            result.put("success", true);
            result.put("total", filePage.getTotal());
            result.put("list", filePage.getRecords());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取文件列表失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 更新安装程序文件信息
     * @param file 文件信息
     * @return 更新结果
     */
    @PutMapping
    public Map<String, Object> updateFile(@RequestBody InstallerFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean updated = installerFileService.updateFile(file);
            if (updated) {
                result.put("code", 200);
                result.put("message", "文件信息更新成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "文件信息更新失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "文件信息更新失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 批量删除安装程序文件
     * @param ids 文件ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Map<String, Object> batchDeleteFiles(@RequestBody List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean deleted = installerFileService.batchDeleteFiles(ids);
            if (deleted) {
                result.put("code", 200);
                result.put("message", "批量删除文件成功");
                result.put("success", true);
            } else {
                result.put("code", 400);
                result.put("message", "批量删除文件失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除文件失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 根据文件ID获取文件详情
     * @param id 文件ID
     * @return 文件详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getFileDetails(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            InstallerFile file = installerFileService.getFileById(id);
            if (file != null) {
                result.put("code", 200);
                result.put("message", "获取文件详情成功");
                result.put("success", true);
                result.put("data", file);
            } else {
                result.put("code", 404);
                result.put("message", "文件不存在");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取文件详情失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 下载安装程序文件
     * @param id 文件ID
     * @return 文件下载响应
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable Integer id) {
        try {
            InstallerFile file = installerFileService.getFileById(id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            File dest = new File(file.getFilePath());
            if (!dest.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getFileName());
            headers.setContentLength(file.getFileSize());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new FileSystemResource(dest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * 删除安装程序文件
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteFile(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            InstallerFile file = installerFileService.getFileById(id);
            if (file == null) {
                result.put("code", 404);
                result.put("message", "文件不存在");
                result.put("success", false);
                return result;
            }
            
            // 删除本地文件
            File dest = new File(file.getFilePath());
            if (dest.exists()) {
                dest.delete();
            }
            
            // 删除数据库记录
            boolean deleted = installerFileService.deleteFile(id);
            
            if (deleted) {
                result.put("code", 200);
                result.put("message", "文件删除成功");
                result.put("success", true);
            } else {
                result.put("code", 500);
                result.put("message", "文件删除失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "文件删除失败：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 计算文件哈希值
     * @param file 上传的文件
     * @return 文件哈希值
     * @throws IOException IO异常
     * @throws NoSuchAlgorithmException 哈希算法异常
     */
    private String calculateFileHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(file.getBytes());
        
        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
