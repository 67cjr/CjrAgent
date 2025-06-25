package com.cjr.cjragent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String charId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好,我是张三";
        String answer = loveApp.doChat(message, charId);
        Assertions.assertNotNull(answer);
        //第二轮
        message = "我想让我的另外一半（李四）更加爱我";
        answer = loveApp.doChat(message, charId);
        Assertions.assertNotNull(answer);
        //第三轮
        message = "我的另外一半叫什么来着？刚刚和你说过，帮我回忆一下";
        answer = loveApp.doChat(message, charId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是张三，我想让另一半（李四）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

}