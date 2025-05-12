package com.ai.agent.superaiagent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 信息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity implements Serializable {


    private String chatId;

    private String content;

    private String type;

}
