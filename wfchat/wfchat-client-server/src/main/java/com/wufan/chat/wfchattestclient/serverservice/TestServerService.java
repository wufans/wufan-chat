package com.wufan.chat.wfchattestclient.serverservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wufan.chat.wfchatcontract.TestService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Deprecated
@Service
public class TestServerService {
    @Reference(version = "1.0.0")
    TestService testService;
    @Bean
    TestService getTestService(){
        return this.testService;
    }
}
