package com.ai.agent.superaiagent.agent;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考-行动的循环模式
 * 具体怎么思考、怎么行动交由子类实现
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent  extends  BaseAgent{

    /**
     * 思考是否执行下一步任务
     * @return
     */
    public abstract boolean think();


    /**
     * 决定要执行的任务
     * @return
     */
    public abstract String act();


    /**
     * 执行单个任务
     * @return
     */
    @Override
    public String step() {

        try {
            boolean shouldAct = think();
            if (!shouldAct){
                return "思考完成-无需执行";
            }
            return act();
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败: " + e.getMessage();
        }
    }
}
