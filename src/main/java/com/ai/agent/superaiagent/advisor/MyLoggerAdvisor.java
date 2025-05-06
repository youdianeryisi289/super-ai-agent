package com.ai.agent.superaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * @Author:sjb
 * @CreateTime:2025-04-29
 * @Description: 自定义日志Advisor
 * 打印info级别日志，只输出单词用户提示词和AI回复的文本
 * @Version：1.0
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    private AdvisedRequest before(AdvisedRequest request){
        log.info("AI request:{}",request.userText());
        return request;
    }

    private void observeAfter(AdvisedResponse response){
        log.info("AI response:{}",response.response().getResult().getOutput().getText());
    }

    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponse = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator().aggregateAdvisedResponse(advisedResponse,this::observeAfter));
    }

    /**
     * 为每个Advisor提供一个唯一标识符
     * @return
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 指定Advisor在链中的执行顺序，值越小优先级越高。越先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
