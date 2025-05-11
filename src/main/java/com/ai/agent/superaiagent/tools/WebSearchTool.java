package com.ai.agent.superaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:sjb
 * @CreateTime:2025-05-11
 * @Description: 网页搜索工具
 * @Version：1.0
 */
public class WebSearchTool {


    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }


    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(@ToolParam(description = "Search query keyword") String query) {

        try {
            // 构造参数
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("q", query);
            paramMap.put("api_key", apiKey);
            paramMap.put("engine","baidu");

            String resource = HttpUtil.get(SEARCH_API_URL, paramMap);

            // 取出返回结果的前5条
            JSONObject jsonObject = JSONUtil.parseObj(resource);
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, 5);

            // 拼接搜索结果为字符串
            String result = objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));

            return result;
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }

    }

}
