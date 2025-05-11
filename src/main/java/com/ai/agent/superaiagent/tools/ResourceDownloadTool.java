package com.ai.agent.superaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.ai.agent.superaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * @Author:sjb
 * @CreateTime:2025-05-11
 * @Description: 资源下载
 * @Version：1.0
 */
public class ResourceDownloadTool {

    @Tool(description = "Download a resource from a given url")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the file to save the downloaded resource") String fileName){

        try {
            String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
            String filePath = fileDir + "/" + fileName;

            FileUtil.mkdir(fileDir);
            HttpUtil.downloadFile(url,new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }

    }


}
