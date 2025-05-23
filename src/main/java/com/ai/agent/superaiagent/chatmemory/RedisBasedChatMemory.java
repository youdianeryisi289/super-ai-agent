package com.ai.agent.superaiagent.chatmemory;

import com.ai.agent.superaiagent.model.ChatEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的对话持久化存储
 */
@Component
@Slf4j
public class RedisBasedChatMemory implements ChatMemory {

    private final RedisTemplate<String,Object> redisTemplate;

    public RedisBasedChatMemory(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    /**
     * key 前缀
     */
    private static final String CHAT_PREFIX = "chat:history:";

    /**
     * 添加对话内容到redis记忆
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = CHAT_PREFIX + conversationId;
        log.info("本次会话构造的redisKey为：{}",key);
        // 转换对话进行序列化存储
        List<ChatEntity> chatEntityList = new ArrayList<>(messages.size());

        for (Message message : messages) {
            String messageText = message.getText();

            ChatEntity chatEntity = new ChatEntity();
            chatEntity.setChatId(conversationId);
            chatEntity.setContent(messageText);
            chatEntity.setType(message.getMessageType().getValue());
            chatEntityList.add(chatEntity);
        }
        for (ChatEntity chatEntity : chatEntityList) {
            redisTemplate.opsForList().rightPush(key,chatEntity);
        }
        redisTemplate.expire(key,30, TimeUnit.MINUTES);
    }

    /**
     * 从redi中取持久化记忆的内容
     * @param conversationId
     * @param lastN
     * @return
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        String key = CHAT_PREFIX + conversationId;
        // 列表当前包含的元素数量
        Long size = redisTemplate.opsForList().size(key);
        if(size == null){
            return Collections.emptyList();
        }

        //  获取聊天对话的最近N条消息
        // size = 2,lastN = 10 ==> start = 0,从第0条开始取
        // size = 12,lastN = 10 ==> start = 2,从第3条开始取
        int start = Math.max(0,(int) (size - lastN));
        // start = 0会返回列表中现存的所有消息
        // start = 2会从列表中的第3条开始到最后全部消息
        List<Object> listTmp = redisTemplate.opsForList().range(key, start, -1);
        List<Message> listOut =  new ArrayList<>(listTmp.size());
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object obj : listTmp) {
            ChatEntity chatEntity = objectMapper.convertValue(obj, ChatEntity.class);
            if (MessageType.USER.getValue().equals(chatEntity.getType())) {
                listOut.add(new UserMessage(chatEntity.getContent()));
            } else if (MessageType.ASSISTANT.getValue().equals(chatEntity.getType())) {
                listOut.add(new AssistantMessage(chatEntity.getContent()));
            } else if (MessageType.SYSTEM.getValue().equals(chatEntity.getContent())) {
                listOut.add(new SystemMessage(chatEntity.getContent()));
            }
        }
        return listOut;
    }

    /**
     * 清除对话记忆
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(CHAT_PREFIX+conversationId);
    }
}
