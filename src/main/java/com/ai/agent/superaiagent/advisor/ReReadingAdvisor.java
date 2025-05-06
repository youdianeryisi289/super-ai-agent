package com.ai.agent.superaiagent.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:sjb
 * @CreateTime:2025-04-29
 * @Description: 重新阅读问题加强能力
 * @Version：1.0
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    /**
     * before来增强用户的输入查询
     * @param advisedRequest
     * @return
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        return AdvisedRequest.from(advisedRequest)
                .userText("""
			    {re2_input_query}
			    Read the question again: {re2_input_query}
			    """)
                .userParams(advisedUserParams)
                .build();
    }

    /**
     * 处理请求并引用re-reading技术
     * @param advisedRequest
     * @param chain
     * @return
     */
    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest,
                                      CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    /**
     * 拦截流请求并应用re-reading技术
     * @param advisedRequest
     * @param chain
     * @return
     */
    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest,
                                              StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 通过order值控制advisor的执行顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
