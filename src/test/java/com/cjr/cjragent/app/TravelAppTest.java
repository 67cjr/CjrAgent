package com.cjr.cjragent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class TravelAppTest {

    @Resource
    private TravelApp travelApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮对话
        String message = "你好，我是小明，我想要去旅行";
        String answer = travelApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮对话
        message = "我想去北京玩，有什么好的景点推荐吗？";
        answer = travelApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第三轮对话 - 测试记忆能力
        message = "我刚才说想去哪个城市来着？";
        answer = travelApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void testChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 测试结构化输出
        String message = "我打算去日本旅游，可以给我一些建议吗？预算10000元，想玩5天";
        TravelApp.TravelReport travelReport = travelApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(travelReport);
        Assertions.assertNotNull(travelReport.destination());
        Assertions.assertNotNull(travelReport.suggestions());
        Assertions.assertTrue(travelReport.suggestions().size() > 0);
    }

    @Test
    void testChatWithCustomVariables() {
        String chatId = UUID.randomUUID().toString();
        // 准备自定义变量
        Map<String, Object> customVariables = new HashMap<>();
        customVariables.put("domesticTravel", "周边游");
        customVariables.put("internationalTravel", "出国游");
        customVariables.put("businessTravel", "商务出差");
        customVariables.put("domesticQuestion", "本地特色美食和隐藏景点");

        // 使用自定义变量进行对话
        String message = "你好，我想了解一下周边游的好去处";
        String answer = travelApp.doChatWithCustomVariables(message, chatId, customVariables);
        Assertions.assertNotNull(answer);
    }
}
