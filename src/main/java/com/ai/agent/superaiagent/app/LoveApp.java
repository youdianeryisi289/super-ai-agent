package com.ai.agent.superaiagent.app;

import com.ai.agent.superaiagent.advisor.MyLoggerAdvisor;
import com.ai.agent.superaiagent.advisor.ReReadingAdvisor;
import com.ai.agent.superaiagent.advisor.SusceptibleAdvisor;
import com.ai.agent.superaiagent.chatmemory.FileBasedChatMemory;
import com.ai.agent.superaiagent.chatmemory.RedisBasedChatMemory;
import com.ai.agent.superaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.ai.agent.superaiagent.rag.QueryRewriter;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @Author:sjb
 * @CreateTime:2025-04-29
 * @Description: 模拟
 * @Version：1.0
 */
@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore loveAppVectorStore;

    /*@Resource
    private VectorStore pgVectorVectorStore;*/

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private QueryRewriter queryRewriter;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";
            //"如果检测到用户输入的问题有违禁词,你应该及时拒绝回答相关问题并给出用户相关友好建议";

    @Value("classpath:/prompts/summarize-prompt.st")
    private org.springframework.core.io.Resource systemResource;

    @Value("classpath:/images/logo.png")
    private org.springframework.core.io.Resource imageResource;


    public LoveApp(ChatModel dashscopeChatModel,RedisTemplate<String,Object> redisTemplate) {
        // 使用基于redis的对话存储
        ChatMemory chatMemory = new RedisBasedChatMemory(redisTemplate);
        chatClient = ChatClient.builder(dashscopeChatModel)
                 .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志Advisor
                        new MyLoggerAdvisor()
                ).build();
    }

    /**
     * 普通对话方法
     * @param chatId
     * @param message
     * @return
     */
    public String doChat(String chatId,String message){
        /*SystemPromptTemplate systemPromptTemplate =  new SystemPromptTemplate(systemResource);
        Message systemtMessage = systemPromptTemplate.
                createMessage(Map.of("role", "医生", "task", "拒绝回答"));
        String messageText = systemtMessage.getText();*/
        //log.info("文件内容的提示词为：{}",messageText);
        ChatResponse response = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new SusceptibleAdvisor())
                .call()
                .chatResponse();
        if (response != null){
            String content = response.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        }
        return null;
    }

    /**
     * record 快速定义特性
     * @param title
     * @param suggestion
     */
    record LoveReport(String title, List<String> suggestion){


    }

    /**
     * rag 知识库聊天
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {

        // 使用查询重写 替换原始的用户输入的message
        String rewriteMessage = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 敏感词校验
                //.advisors(new SusceptibleAdvisor())
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                /*.advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(pgVectorVectorStore,"married")
                )*/
                // 云端知识库问答
                //.advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * todo 生成报告待完善
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message,String chatId){

        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .call()
                .entity(LoveReport.class);

        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    /**
     * 使用工具聊天
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    /**
     * 多模态聊天测试
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMultiModel(String message,String  chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(u -> u.text(message).media(MimeTypeUtils.IMAGE_PNG, imageResource))
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .call().chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content内容为：{}",content);
        return content;
    }


    public String doChatWithMcp(String chatId,String message){
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .tools(toolCallbackProvider)
                //.advisors(new SusceptibleAdvisor())
                .call()
                .chatResponse();
        if (response != null){
            String content = response.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        }
        return null;
    }

    /**
     * 支持流式调用输出
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }
}
