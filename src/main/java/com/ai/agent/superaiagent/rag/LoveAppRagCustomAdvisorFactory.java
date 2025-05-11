package com.ai.agent.superaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * @Author:sjb
 * @CreateTime:2025-05-10
 * @Description: 根据用户查询需求生成对应的advisor
 * @Version：1.0
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {


    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore,String status){
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status).build();

        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder().vectorStore(vectorStore)
                .filterExpression(expression)  // 过滤条件
                .similarityThreshold(0.5)  // 相似度
                .topK(3)   // topN 数量
                .build();

        return RetrievalAugmentationAdvisor.builder().documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }

}
