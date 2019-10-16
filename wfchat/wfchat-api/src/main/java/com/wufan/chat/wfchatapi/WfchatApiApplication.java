package com.wufan.chat.wfchatapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 服务发布者的启动类
 */
@ImportResource({"classpath:dubbo-provider.xml"})
@MapperScan(basePackages = "com.wufan.chat.wfchatrepository.mapper")
@SpringBootApplication
public class WfchatApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WfchatApiApplication.class, args);
    }

}
