package com.ai.agent.superaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileOperationTest {



    @Test
    public void testReadFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "测试.txt";
        String result = tool.readFile(fileName);
        assertNotNull(result);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "测试.txt";
        String content = "https://www.baidu.cn 这是一个测试文件";
        String result = tool.writeFile(fileName, content);
        assertNotNull(result);
    }

}