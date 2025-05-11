package com.ai.agent.superaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.ai.agent.superaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @Author:sjb
 * @CreateTime:2025-05-11
 * @Description: 文件操作工具类，注解式
 * @Version：1.0
 */
public class FileOperationTool {


    private final String FILE_PATH = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String filePath = FILE_PATH + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (IORuntimeException e) {
            return "Error read file: " + e.getMessage();
        }
    }


    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {

        String filePath = FILE_PATH + "/" + fileName;

        // 创建目录
        try {
            FileUtil.mkdir(FILE_PATH);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (IORuntimeException e) {
            return "Error writing file: " + e.getMessage();
        }


    }

}
