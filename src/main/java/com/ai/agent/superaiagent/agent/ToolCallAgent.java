package com.ai.agent.superaiagent.agent;


import cn.hutool.core.collection.CollUtil;
import com.ai.agent.superaiagent.model.enums.AgentEum;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，
 * 在reAct模式上增加了工具调用能力
 * 具体实现了 think 和 act 方法
 * 可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    /**
     * 可以调用的工具
     */
    private final ToolCallback[] toolCallbackProvider;

    // 工具调用的响应信息
    private ChatResponse toolCallChatResponse;

    // 工具调用的管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用内置的工具调用机制，自己维护上下文
    private final ChatOptions chatOptions;


    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.toolCallbackProvider = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    /**
     * 和AI交互思考使用什么工具
     * @return
     */
    @Override
    public boolean think() {

        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()){
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getChatMessages().add(userMessage);
        }

        List<Message> messageList = getChatMessages();
        Prompt prompt = new Prompt(messageList, chatOptions);


        try {
            // 获取带工具选项的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(toolCallbackProvider)
                    .call()
                    .chatResponse();

            // 记录响应 用于reAct
            this.toolCallChatResponse = chatResponse;
            AssistantMessage assistantMessage = null;
            String result = null;
            List<AssistantMessage.ToolCall> toolCallList = null;
            if(chatResponse != null){
               assistantMessage =  chatResponse.getResult().getOutput();
               // 输出提示信息
               result = assistantMessage.getText();
               toolCallList = assistantMessage.getToolCalls();
               if (toolCallList != null){
                   // 输出提示信息
                   log.info(getName() + "的思考: " + result);
                   log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");

                   String toolCallInfo = toolCallList.stream()
                           .map(toolCall -> String.format("工具名称：%s，参数：%s",
                                   toolCall.name(),
                                   toolCall.arguments())
                           )
                           .collect(Collectors.joining("\n"));
                   log.info(toolCallInfo);

               }
            }
            if (toolCallList.isEmpty()){
                // 只有不调用工具时，才记录助手消息
                getChatMessages().add(assistantMessage);
                return false;
            }else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            getChatMessages().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 程序执行工具
     * 执行工具调用列表，得到返回结果，并将工具的响应添加到消息列表中：
     * @return
     */
    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()){
            return "没有工具调用";
        }
        // 调用工具
        Prompt prompt = new Prompt(getChatMessages(),chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        /**
         * 注意维护消息上下文，不要重复添加了消息，
         * 因为 toolExecutionResult.conversationHistory() 方法已经包含了助手消息和工具调用返回的结果。
         */
        setChatMessages(toolExecutionResult.conversationHistory());
        // 当前工具的调用结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        // 判断是否调用了终止工具
        boolean anyMatch = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (anyMatch){
            setState(AgentEum.FINISHED);
        }
        log.info(results);
        return results;
    }
}
