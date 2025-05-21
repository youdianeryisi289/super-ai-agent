package com.mcp.weather.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.weather.model.CityWeatherInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * MCP 天气服务
 */
@Service
@Slf4j
public class WeatherService {


    // 请求接口的示例 https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=<用户key>
    @Value("${gaode.weather.api_key}")
    private String weatherApiKey;

    @Value("${gaode.weather.request_url}")
    private String weatherBaseRequestUrl;


    /**
     * 查询城市天气信息
     * @param cityCode
     * @return
     */
    @Tool(description = "Search weather information by city name")
    public String getWeatherInfoFromGaoDe(@ToolParam(description = "Search query key word") String cityCode) {
        String requestUrl = weatherBaseRequestUrl + "city=" + cityCode + "&key=" + weatherApiKey;
        log.info("本次发起请求查询天气的地址为：{}", requestUrl);

        String result = null;
        try {
            result = HttpUtil.get(requestUrl);
        } catch (Exception e) {
            log.error("请求查询天气信息API失败：{}",e);
        }
        CityWeatherInfoDto cityWeatherInfoDto = null;
        JSONObject resultJson = JSONUtil.parseObj(result);
        String status = resultJson.getStr("status");
        String info = resultJson.getStr("info");
        if ("1".equals(status) && StrUtil.equalsIgnoreCase("OK",info)){
            // 接口响应成功
            JSONArray livesInfo = resultJson.getJSONArray("lives");
            Object cityWeatherInfo = livesInfo.getFirst();
            JSONObject jsonObject = JSONUtil.parseObj(cityWeatherInfo);
            try {
                cityWeatherInfoDto = JSONUtil.toBean(jsonObject, CityWeatherInfoDto.class);
                if (cityWeatherInfoDto !=  null){

                }
            } catch (IllegalArgumentException e) {
                log.error("天气信息序列化格式错误：{}",e);
            }
        }else{
            log.info("请求查询天气信息API的status响应非1");
            return null;
        }
        return JSONUtil.toJsonStr(cityWeatherInfoDto);
    }

}
