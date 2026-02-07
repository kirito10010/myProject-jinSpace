package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.City;
import java.util.List;

/**
 * 城市Service
 */
public interface CityService extends IService<City> {

    /**
     * 根据父级ID获取子级城市列表
     * @param pid 父级ID
     * @return 子级城市列表
     */
    List<City> getCitiesByPid(Long pid);
}
