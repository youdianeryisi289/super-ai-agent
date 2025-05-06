package com.ai.agent.superaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author:sjb
 * @CreateTime:2025-04-25
 * @Description: Spring AI 测试
 * @Version：1.0
 */
@Component
public class SpringAIInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        /*AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是马牛"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());*/
    }
}
