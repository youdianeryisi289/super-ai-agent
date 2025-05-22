package com.ai.agent.superaiagent.agent;


import cn.hutool.core.util.StrUtil;
import com.ai.agent.superaiagent.model.enums.AgentEum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jacoco.agent.rt.internal_f3994fa.Agent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 * 包含 chatClient 属性，由调用方传入具体调用大模型的对象，而不是写死使用的大模型，更灵活
 * 包含 messageList 属性，用于维护消息上下文列表
 * 通过 state 属性来控制智能体的执行流程
 */
@Data
@Slf4j
public abstract class BaseAgent {


    /**
     * 核心属性
     */
    private String name;

    private String systemPrompt;
    private String nextStepPrompt;


    private AgentEum state = AgentEum.IDLE;

    private int currentStep = 0;
    private int maxStep = 10;

    // LLM
    private ChatClient chatClient;

    private List<Message> chatMessages = new ArrayList<>();


    public String run(String userPrompt) {

        if (this.state != AgentEum.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }

        if (StrUtil.isEmpty(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }

        // 更改状态
        state = AgentEum.RUNNING;
        // 记录上下文信息
        chatMessages.add(new UserMessage(userPrompt));

        // 保存结果列表
        List<String> results = new ArrayList<>();


        try {
            for (int i = 0; i < maxStep && state != AgentEum.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step " + stepNumber + "/" + maxStep);
                // 单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }

            // 检查是否超出步骤限制
            if (currentStep >= maxStep) {
                state = AgentEum.FINISHED;
                results.add("Terminated: Reached max steps (" + maxStep + ")");
            }

            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentEum.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();

        } finally {
            this.cleanup();
        }
    }


    /**
     * 子类执行单个步骤
     *
     * @return 步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        // 子类可以重写此方法来清理资源
    }


}
