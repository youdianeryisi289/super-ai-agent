package com.ai.agent.superaiagent.advisor;

import com.ai.agent.superaiagent.loader.SensitiveWordsLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author:sjb
 * @CreateTime:2025-05-05
 * @Description: 自定义敏感词advisor
 * @Version：1.0
 */
@Slf4j
public class SusceptibleAdvisor implements StreamAroundAdvisor, CallAroundAdvisor {


    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedRequest request = processSensitiveWords(advisedRequest);
        return chain.nextAroundCall(request);
    }

    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(processSensitiveWords(advisedRequest));
    }

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return -100;
    }


    public AdvisedRequest processSensitiveWords(AdvisedRequest advisedRequest) {
        String userText = advisedRequest.userText();
        boolean matched = false;
        try {
            List<String> sensitiveWords = SensitiveWordsLoader.getSensitiveWordsFromFile();
            matched = sensitiveWords.stream().anyMatch(userText::contains);
        } catch (IOException e) {
            log.error("获取文件敏感词异常：{}",e);
        }

        log.info("匹配到敏感词：{}", matched);
        if (matched) {
            userText = "用户输入包含违禁词," + userText;
            return AdvisedRequest.builder()
                    .chatModel(advisedRequest.chatModel())
                    .chatOptions(advisedRequest.chatOptions())
                    .systemText(advisedRequest.systemText())
                    .advisors(advisedRequest.advisors())
                    .userText(advisedRequest.userText())
                    .userParams(advisedRequest.userParams())
                    .adviseContext(advisedRequest.adviseContext())
                    .functionNames(advisedRequest.functionNames())
                    .media(advisedRequest.media())
                    .messages(advisedRequest.messages())
                    .functionCallbacks(advisedRequest.functionCallbacks())
                    .systemParams(advisedRequest.systemParams())
                    .toolContext(advisedRequest.toolContext())
                    .userText(userText).build();
        }
        return advisedRequest;
    }
}
