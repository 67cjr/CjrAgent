package com.cjr.cjragent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpAiInvoke {
    public static void main(String[] args) {
        // 设置新的URL
        String url = "your_new_api_endpoint_here"; // 替换为实际的API端点

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("encrypt", "");
        headers.put("sign", "");

        // 设置请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("instruct", "");
        requestBody.put("model", " "); // 替换为实际的模型名称

        // 创建消息数组
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "你好");
        messages.add(userMessage);

        requestBody.put("messages", messages);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .body(requestBody.toString())
                .execute();

        // 处理响应
        if (response.isOk()) {
            System.out.println("请求成功，响应内容：");
            System.out.println(response.body());
        } else {
            System.out.println("请求失败，状态码：" + response.getStatus());
            System.out.println("响应内容：" + response.body());
        }
    }
}