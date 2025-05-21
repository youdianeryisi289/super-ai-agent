package com.mcp.weather.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author:sjb
 * @CreateTime:2025-04-25
 * @Description: 健康检查接口
 * @Version：1.0
 */
@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {


    @GetMapping("/check")
    public String check(){
        log.info("接口检查");
        return "OK";
    }

}
