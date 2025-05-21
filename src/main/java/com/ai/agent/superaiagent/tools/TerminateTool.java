package com.ai.agent.superaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;

/**
 * 终止任务工具
 * 让智能体自行决定结束任务
 */
public class TerminateTool {

    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)
    public String doTerminate() {
        return "任务结束";
    }
}
