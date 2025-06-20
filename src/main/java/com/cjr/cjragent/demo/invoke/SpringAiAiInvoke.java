package com.cjr.cjragent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;

// 取消注释即可在 SpringBoot 项目启动时执行
//@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我该如何学习java"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}

