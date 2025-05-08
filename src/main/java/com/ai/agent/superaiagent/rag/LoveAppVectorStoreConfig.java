package com.ai.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author:sjb
 * @CreateTime:2025-04-30
 * @Description: 初始化向量数据库并保存文档
 * @Version：1.0
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private BatchingStrategy customTokenCountBatchingStrategy;


    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkDown();
        // 批处理文档
        List<List<Document>> batched = customTokenCountBatchingStrategy.batch(documents);

        // 分批添加到向量数据库
        for (List<Document> documentList : batched) {
            simpleVectorStore.add(documentList);
        }
        return simpleVectorStore;
    }
}
