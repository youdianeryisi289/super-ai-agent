package com.ai.agent.superaiagent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    public void doTestChat(){

        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是罗密欧，";
        String answer = loveApp.doChat(chatId, message);
        Assertions.assertNotNull(answer);
      // 第二轮
        message = "这些诗人的出生地在哪？";
        answer = loveApp.doChat(chatId, message);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "刚才你给我推荐的爱情的诗人叫什么来着？帮我回忆一下";
        answer = loveApp.doChat(chatId, message);
        Assertions.assertNotNull(answer);

    }

    @Test
    void doChatWithReport(){
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是罗密欧，我想让另一半（朱丽叶）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我在约会，怎样给恋爱中的对方制造浪漫惊喜？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
        message = "你是基于什么样的考虑给我推荐这样的方式？";
        answer = loveApp.doChatWithRag(message,chatId);
        Assertions.assertNotNull(answer);
        message = "在使用这些方式的时候，我需要注意什么？";
        answer = loveApp.doChatWithRag(message,chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        //testMessage("最近和对象吵架了，看看百度上面（baidu.com）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        //testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        //testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        //testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        //testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }




    private void testMessage(String message){
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

}