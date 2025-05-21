package com.ai.agent.superaiagent.model.enums;

import lombok.Getter;

/**
 * 代理执行状态的枚举类
 */
@Getter
public enum AgentEum {
    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 运行中状态
     */
    RUNNING,

    /**
     * 已完成状态
     */
    FINISHED,

    /**
     * 错误状态
     */
    ERROR
}
