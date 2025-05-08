package com.ai.agent.superaiagent.config;

import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:sjb
 * @CreateTime:2025-05-08
 * @Description: 嵌入向量数据库配置
 * @Version：1.0
 */
@Configuration
public class EmbeddingConfig {


    @Bean
    public BatchingStrategy customTokenCountBatchingStrategy() {
        return new TokenCountBatchingStrategy(
                EncodingType.CL100K_BASE,  // 指定编码类型
                8000,                      // 设置最大输入标记计数
                0.1                        // 设置保留百分比
        );
    }

}
