package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.ProductionProject;

import java.util.List;

/**
 * 生产项目服务接口
 * 继承IService，提供基本的CRUD操作
 */
public interface ProductionProjectService extends IService<ProductionProject> {
    
    /**
     * 获取用户的所有生产项目
     * @param uid 用户ID
     * @return 生产项目列表
     */
    List<ProductionProject> getAllProjectsByUid(Integer uid);
    
    /**
     * 根据ID和用户ID获取生产项目
     * @param id 项目ID
     * @param uid 用户ID
     * @return 生产项目
     */
    ProductionProject getProjectByIdAndUid(Integer id, Integer uid);
    
    /**
     * 添加生产项目
     * @param project 生产项目
     * @return 是否添加成功
     */
    boolean addProject(ProductionProject project);
    
    /**
     * 修改生产项目
     * @param project 生产项目
     * @param uid 用户ID
     * @return 是否修改成功
     */
    boolean updateProject(ProductionProject project, Integer uid);
    
    /**
     * 删除生产项目
     * @param id 项目ID
     * @param uid 用户ID
     * @return 是否删除成功
     */
    boolean deleteProject(Integer id, Integer uid);
    
    /**
     * 批量删除生产项目
     * @param ids 项目ID列表
     * @param uid 用户ID
     * @return 是否删除成功
     */
    boolean batchDeleteProjects(List<Integer> ids, Integer uid);
}