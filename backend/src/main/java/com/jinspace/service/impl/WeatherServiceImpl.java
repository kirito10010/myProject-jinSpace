package com.jinspace.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jinspace.service.WeatherService;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 天气服务实现类
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    // API固定参数
    private static final String API_URL = "http://api.zhyunxi.com/api.php";
    private static final String API_ID = "11";
    private static final String API_KEY = "e6748b1e2aba0ac10737b216b3960c71";

    /**
     * 根据城市名称查询天气
     * @param cityName 城市名称
     * @return 天气数据
     */
    @Override
    public Map<String, Object> getWeatherByCityName(String cityName) {
        // 构建请求参数
        String url = API_URL + "?api=" + API_ID + "&key=" + API_KEY + "&city=" + cityName;
        
        // 发送HTTP请求
        String result = HttpUtil.get(url);
        
        // 解析JSON响应
        JSONObject jsonObject = JSONUtil.parseObj(result);
        return jsonObject;
    }
}
