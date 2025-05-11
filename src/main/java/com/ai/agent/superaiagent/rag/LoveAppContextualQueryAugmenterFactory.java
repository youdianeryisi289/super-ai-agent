package com.ai.agent.superaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * @Author:sjb
 * @CreateTime:2025-05-10
 * @Description: 自定义错误处理逻辑，允许空的上下文
 * @Version：1.0
 */
public class LoveAppContextualQueryAugmenterFactory {



    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate emptyContextPromptTemplate =  new PromptTemplate(
                """
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以找波波哦！
                """
        );
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }

}
