package com.jinspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.ProductionProject;
import com.jinspace.mapper.ProductionProjectMapper;
import com.jinspace.service.ProductionProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 生产项目服务实现类
 * 实现ProductionProjectService接口，提供具体的业务逻辑
 */
@Service
public class ProductionProjectServiceImpl extends ServiceImpl<ProductionProjectMapper, ProductionProject> implements ProductionProjectService {
    
    @Override
    public ProductionProjectMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    @Override
    public List<ProductionProject> getAllProjectsByUid(Integer uid) {
        QueryWrapper<ProductionProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return list(queryWrapper);
    }
    
    @Override
    public ProductionProject getProjectByIdAndUid(Integer id, Integer uid) {
        QueryWrapper<ProductionProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("uid", uid);
        return getOne(queryWrapper);
    }
    
    @Override
    public boolean addProject(ProductionProject project) {
        return save(project);
    }
    
    @Override
    public boolean updateProject(ProductionProject project, Integer uid) {
        // 先检查项目是否存在且属于当前用户
        ProductionProject existingProject = getProjectByIdAndUid(project.getId(), uid);
        if (existingProject == null) {
            return false;
        }
        return updateById(project);
    }
    
    @Override
    public boolean deleteProject(Integer id, Integer uid) {
        // 先检查项目是否存在且属于当前用户
        ProductionProject existingProject = getProjectByIdAndUid(id, uid);
        if (existingProject == null) {
            return false;
        }
        return removeById(id);
    }
    
    @Override
    public boolean batchDeleteProjects(List<Integer> ids, Integer uid) {
        // 构建查询条件，只删除属于当前用户的项目
        QueryWrapper<ProductionProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids).eq("uid", uid);
        return remove(queryWrapper);
    }
}