package com.ai.agent.superaiagent.advisor;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.core.io.support.ResourcePatternResolver;
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

    //private final Set<String> sensitiveWords = Set.of("暴力", "涉黄", "敏感词1", "敏感词2");

    @Resource
    private ResourcePatternResolver resourcePatternResolver;

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

    /**
     * 从文件读取高敏感词内容
     * @return
     * @throws IOException
     */
    public List<String> getSensitiveWordsFromFile() throws IOException {
        org.springframework.core.io.Resource resource =
                resourcePatternResolver.getResource("classpath:sensitive/sensitiveFile");
        // 2. 读取文件内容（兼容Jar包内资源）
        String content;
        try (InputStream inputStream = resource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            // 读取全部内容
            content = scanner.useDelimiter("\\A").next();
            // 3. 按逗号分割并去除空格
            // 过滤空字符串
            List<String> sensitiveWords = Arrays.stream(content.split(","))
                    .map(String::trim)
                    // 过滤空字符串
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());
            return sensitiveWords;
        }
    }

    public AdvisedRequest processSensitiveWords(AdvisedRequest advisedRequest) {
        String userText = advisedRequest.userText();
        boolean matched = false;
        try {
            List<String> sensitiveWords = getSensitiveWordsFromFile();
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
                    .userText(userText).build();
        }
        return advisedRequest;
    }
}
