package com.jinspace.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.InstallerFile;

import java.util.List;

/**
 * 安装程序文件Service接口
 * 继承IService，使用MyBatis Plus提供的Service层方法
 */
public interface InstallerFileService extends IService<InstallerFile> {
    
    /**
     * 根据用户ID获取安装程序文件列表
     * @param userId 用户ID
     * @return 安装程序文件列表
     */
    List<InstallerFile> getFilesByUserId(Integer userId);
    
    /**
     * 根据用户ID分页获取安装程序文件列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<InstallerFile> getFilesByUserId(Integer userId, int page, int pageSize);
    
    /**
     * 根据文件ID获取文件信息
     * @param id 文件ID
     * @return 安装程序文件信息
     */
    InstallerFile getFileById(Integer id);
    
    /**
     * 保存安装程序文件信息
     * @param file 文件信息
     * @return 是否保存成功
     */
    boolean saveFile(InstallerFile file);
    
    /**
     * 更新安装程序文件信息
     * @param file 文件信息
     * @return 是否更新成功
     */
    boolean updateFile(InstallerFile file);
    
    /**
     * 删除安装程序文件
     * @param id 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Integer id);
    
    /**
     * 批量删除安装程序文件
     * @param ids 文件ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteFiles(List<Integer> ids);
    
    /**
     * 更新文件状态
     * @param id 文件ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateFileStatus(Integer id, String status);
}
