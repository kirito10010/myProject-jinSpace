package com.jinspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.City;
import com.jinspace.mapper.CityMapper;
import com.jinspace.service.CityService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 城市Service实现类
 */
@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Override
    public CityMapper getBaseMapper() {
        return super.getBaseMapper();
    }

    /**
     * 根据父级ID获取子级城市列表
     * @param pid 父级ID
     * @return 子级城市列表
     */
    @Override
    public List<City> getCitiesByPid(Long pid) {
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", pid);
        // 根据父级ID动态判断城市类型
        if (pid == 10000000L) {
            // 全国的ID是10000000，获取所有省份（type=1）
            queryWrapper.eq("type", 1);
        } else {
            // 获取省份下的城市（type=2）或城市下的区县（type=3）
            // 这里根据父级的type来判断子级的type
            // 首先查询父级的type
            City parent = baseMapper.selectById(pid);
            if (parent != null) {
                int parentType = Integer.parseInt(parent.getType());
                queryWrapper.eq("type", parentType + 1);
            }
        }
        queryWrapper.orderByAsc("id");
        return baseMapper.selectList(queryWrapper);
    }
}
