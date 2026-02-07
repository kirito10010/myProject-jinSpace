package com.jinspace.controller;

import com.jinspace.entity.City;
import com.jinspace.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 城市控制器
 */
@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    /**
     * 获取省份列表
     * @return 省份列表
     */
    @GetMapping("/provinces")
    public List<City> getProvinces() {
        // 全国的ID是10000000，获取所有省份（type=1）
        return cityService.getCitiesByPid(10000000L);
    }

    /**
     * 根据省份ID获取城市列表
     * @param provinceId 省份ID
     * @return 城市列表
     */
    @GetMapping("/cities/{provinceId}")
    public List<City> getCities(@PathVariable Long provinceId) {
        return cityService.getCitiesByPid(provinceId);
    }

    /**
     * 根据城市ID获取区县列表
     * @param cityId 城市ID
     * @return 区县列表
     */
    @GetMapping("/counties/{cityId}")
    public List<City> getCounties(@PathVariable Long cityId) {
        return cityService.getCitiesByPid(cityId);
    }
}
