package com.ai.agent.superaiagent.controller;


import com.ai.agent.superaiagent.agent.LoveManus;
import com.ai.agent.superaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RequestMapping("/ai")
@RestController
@Slf4j
public class AIController {


    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 前端暂时没调用这个接口
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }


    /**
     * SSE响应 返回 Flux响应式对象
     * 恋爱大师请求接口
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        log.info("本次会话的message:{},chatId:{}",message,chatId);
        return loveApp.doChatByStream(message, chatId);
    }


    /**
     * 流式调用 Manus 超级智能体
     * 超级智能体求接口
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        LoveManus loveManus = new LoveManus(allTools, dashscopeChatModel);
        return loveManus.runWithStream(message);
    }
}
