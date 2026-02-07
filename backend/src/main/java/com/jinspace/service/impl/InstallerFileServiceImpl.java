package com.jinspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.InstallerFile;
import com.jinspace.mapper.InstallerFileMapper;
import com.jinspace.service.InstallerFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 安装程序文件Service实现类
 * 实现InstallerFileService接口
 */
@Service
public class InstallerFileServiceImpl extends ServiceImpl<InstallerFileMapper, InstallerFile> implements InstallerFileService {
    
    @Autowired
    private InstallerFileMapper installerFileMapper;
    
    @Override
    public InstallerFileMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    /**
     * 根据用户ID获取安装程序文件列表
     * @param userId 用户ID
     * @return 安装程序文件列表
     */
    @Override
    public List<InstallerFile> getFilesByUserId(Integer userId) {
        QueryWrapper<InstallerFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("upload_time");
        return installerFileMapper.selectList(queryWrapper);
    }
    
    /**
     * 根据用户ID分页获取安装程序文件列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public Page<InstallerFile> getFilesByUserId(Integer userId, int page, int pageSize) {
        // 创建分页对象
        Page<InstallerFile> filePage = new Page<>(page, pageSize);
        
        // 创建查询条件
        QueryWrapper<InstallerFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("upload_time");
        
        // 执行分页查询
        return installerFileMapper.selectPage(filePage, queryWrapper);
    }
    
    /**
     * 根据文件ID获取文件信息
     * @param id 文件ID
     * @return 安装程序文件信息
     */
    @Override
    public InstallerFile getFileById(Integer id) {
        return installerFileMapper.selectById(id);
    }
    
    /**
     * 保存安装程序文件信息
     * @param file 文件信息
     * @return 是否保存成功
     */
    @Override
    public boolean saveFile(InstallerFile file) {
        return this.save(file);
    }
    
    /**
     * 更新安装程序文件信息
     * @param file 文件信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateFile(InstallerFile file) {
        return this.updateById(file);
    }
    
    /**
     * 删除安装程序文件
     * @param id 文件ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(Integer id) {
        // 获取文件信息，用于删除本地文件
        InstallerFile file = this.getById(id);
        if (file != null) {
            // 删除本地文件
            java.io.File dest = new java.io.File(file.getFilePath());
            if (dest.exists()) {
                dest.delete();
            }
        }
        // 删除数据库记录
        return this.removeById(id);
    }
    
    /**
     * 批量删除安装程序文件
     * @param ids 文件ID列表
     * @return 是否删除成功
     */
    @Override
    public boolean batchDeleteFiles(List<Integer> ids) {
        // 批量获取文件信息
        List<InstallerFile> files = this.listByIds(ids);
        // 批量删除本地文件
        for (InstallerFile file : files) {
            java.io.File dest = new java.io.File(file.getFilePath());
            if (dest.exists()) {
                dest.delete();
            }
        }
        // 批量删除数据库记录
        return this.removeByIds(ids);
    }
    
    /**
     * 更新文件状态
     * @param id 文件ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    public boolean updateFileStatus(Integer id, String status) {
        InstallerFile file = new InstallerFile();
        file.setId(id);
        file.setStatus(status);
        return this.updateById(file);
    }
}
