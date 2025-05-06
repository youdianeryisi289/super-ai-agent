package com.ai.agent.superaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author:sjb
 * @CreateTime:2025-04-25
 * @Description: HTTP调用示例
 * @Version：1.0
 */
public class HttpInvoke {

    public static void main(String[] args) {
        String apiKey = "sk-effdc91db4234d1aa416e3d633a95d77";
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

        // 构建请求体JSON
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");

        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");

        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");

        requestBody.set("messages", JSONUtil.createArray().put(systemMessage).put(userMessage));

        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();
        // 输出响应结果
        System.out.println(response.body());
    }
}
