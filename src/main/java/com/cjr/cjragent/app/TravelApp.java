package com.cjr.cjragent.app;

import com.cjr.cjragent.advisor.MyLoggerAdvisor;
import com.cjr.cjragent.chatmemory.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class TravelApp {

    private final ChatClient chatClient;
    private final SystemPromptTemplate systemPromptTemplate;

    /**
     * 初始化 ChatClient
     * @param dashscopeChatModel 聊天模型
     * @param travelGuideResource 旅行向导提示模板资源
     */
    public TravelApp(ChatModel dashscopeChatModel,
                    @Value("classpath:/prompts/travel-guide.st") Resource travelGuideResource) {
        // 初始化基于文件存储的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        // 从资源文件创建系统提示模板
        this.systemPromptTemplate = new SystemPromptTemplate(travelGuideResource);

        // 准备模板变量
        Map<String, Object> templateVariables = getDefaultTemplateVariables();

        // 创建ChatClient
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPromptTemplate.render(templateVariables))
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志 Advisor
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * 准备默认的模板变量
     * @return 模板变量映射
     */
    private Map<String, Object> getDefaultTemplateVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("domesticTravel", "国内旅行");
        variables.put("internationalTravel", "国际旅行");
        variables.put("businessTravel", "商务旅行");
        variables.put("domesticQuestion", "景点推荐、交通方式和本地特色美食");
        variables.put("internationalQuestion", "签证事宜、汇率兑换和当地文化习俗");
        variables.put("businessQuestion", "会议安排、商务礼仪和行程优化");
        variables.put("currentSeason", getCurrentSeason());
        return variables;
    }

    /**
     * 获取当前季节
     * @return 当前季节字符串
     */
    private String getCurrentSeason() {
        Month currentMonth = LocalDate.now().getMonth();
        if (currentMonth == Month.MARCH || currentMonth == Month.APRIL || currentMonth == Month.MAY) {
            return "春季";
        } else if (currentMonth == Month.JUNE || currentMonth == Month.JULY || currentMonth == Month.AUGUST) {
            return "夏季";
        } else if (currentMonth == Month.SEPTEMBER || currentMonth == Month.OCTOBER || currentMonth == Month.NOVEMBER) {
            return "秋季";
        } else {
            return "冬季";
        }
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message 用户输入的消息
     * @param chatId 对话 ID，用于标识会话
     * @return AI 的回复内容
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 旅行报告（实战结构化输出）
     * 定义一个数据类 TravelReport，包含目的地和行程建议列表
     * @param destination 目的地
     * @param suggestions 行程建议列表
     */
    record TravelReport(String destination, List<String> suggestions) {
    }

    /**
     * 带有结构化输出的聊天
     * @param message 用户输入的消息
     * @param chatId 对话 ID
     * @return 旅行报告
     */
    public TravelReport doChatWithReport(String message, String chatId) {
        // 使用自定义变量更新系统提示
        Map<String, Object> variables = getDefaultTemplateVariables();

        TravelReport travelReport = chatClient
                .prompt()
                .system(systemPromptTemplate.render(variables) +
                        "每次对话后都要生成旅行结果，标题为目的地，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(TravelReport.class);
        log.info("travelReport: {}", travelReport);
        return travelReport;
    }

    /**
     * 使用自定义变量进行聊天
     * @param message 用户输入的消息
     * @param chatId 对话 ID
     * @param customVariables 自定义模板变量
     * @return AI 的回复内容
     */
    public String doChatWithCustomVariables(String message, String chatId, Map<String, Object> customVariables) {
        // 合并默认变量和自定义变量
        Map<String, Object> variables = getDefaultTemplateVariables();
        variables.putAll(customVariables);

        ChatResponse response = chatClient
                .prompt()
                .system(systemPromptTemplate.render(variables))
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
