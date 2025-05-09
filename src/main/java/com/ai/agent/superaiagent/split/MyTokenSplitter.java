package com.ai.agent.superaiagent.split;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyTokenSplitter {

    /**
     * TokenTextSplitter  切割文档
     * @param documents
     * @return
     */
    public List<Document> splitDocument(List<Document> documents){
        TokenTextSplitter  splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    /**
     * 自定义切割
     * @param documents
     * @return
     */
    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
        return splitter.apply(documents);
    }

}
