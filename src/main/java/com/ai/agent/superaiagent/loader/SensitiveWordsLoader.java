package com.ai.agent.superaiagent.loader;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @Author:sjb
 * @CreateTime:2025-05-06
 * @Description: 敏感词加载
 * @Version：1.0
 */
@Slf4j
@Component
public class SensitiveWordsLoader {


    private static ResourcePatternResolver resourcePatternResolver;

    SensitiveWordsLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    /**
     * 从文件读取高敏感词内容
     *
     * @return
     * @throws IOException
     */
    public static List<String> getSensitiveWordsFromFile() throws IOException {
        org.springframework.core.io.Resource resource =
                resourcePatternResolver.getResource("classpath:sensitive/sensitiveFile");
        // 2. 读取文件内容（兼容Jar包内资源）
        String content;
        try (InputStream inputStream = resource.getInputStream();
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            // 读取全部内容
            content = scanner.useDelimiter("\\A").next();
            // 3. 按逗号分割并去除空格
            // 过滤空字符串
            List<String> sensitiveWords = Arrays.stream(content.split(","))
                    .map(String::trim)
                    // 过滤空字符串
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());
            log.info("文件中加载的敏感词为: {}", sensitiveWords);
            return sensitiveWords;
        }
    }


}
