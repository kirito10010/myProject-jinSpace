package com.jinspace.service;

import java.util.Map;

/**
 * 天气服务
 */
public interface WeatherService {

    /**
     * 根据城市名称查询天气
     * @param cityName 城市名称
     * @return 天气数据
     */
    Map<String, Object> getWeatherByCityName(String cityName);
}
