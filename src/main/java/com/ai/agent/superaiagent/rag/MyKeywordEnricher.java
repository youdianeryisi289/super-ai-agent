package com.ai.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author:sjb
 * @CreateTime:2025-05-10
 * @Description: 自主添加元信息
 * @Version：1.0
 */
@Component
public class MyKeywordEnricher {



    @Resource
    private ChatModel dashscopeChatModel;


    public List<Document> enrichDocument(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel,5);
        return keywordMetadataEnricher.apply(documents);
    }
}
