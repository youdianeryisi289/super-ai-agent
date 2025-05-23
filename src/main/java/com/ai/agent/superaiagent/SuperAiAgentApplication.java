package com.ai.agent.superaiagent;

import org.springframework.ai.autoconfigure.anthropic.AnthropicAutoConfiguration;
import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

/**
 * @author youyisi
 */
//@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
        ,PgVectorStoreAutoConfiguration.class, AnthropicAutoConfiguration.class})
public class SuperAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperAiAgentApplication.class, args);
    }

}
