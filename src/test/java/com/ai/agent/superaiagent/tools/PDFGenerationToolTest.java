package com.ai.agent.superaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "测试.pdf";
        String content = "测试 https://www.baidu.com";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }

}