package com.jinspace.controller;

import com.jinspace.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 天气控制器
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * 根据城市名称查询天气
     * @param cityName 城市名称
     * @return 天气数据
     */
    @GetMapping
    public Map<String, Object> getWeather(@RequestParam String cityName) {
        return weatherService.getWeatherByCityName(cityName);
    }
}
