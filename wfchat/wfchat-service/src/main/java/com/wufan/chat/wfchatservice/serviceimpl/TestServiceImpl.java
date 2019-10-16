package com.wufan.chat.wfchatservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wufan.chat.wfchatcontract.TestService;

@Service(version = "1.0.0")
public class TestServiceImpl implements TestService {

    @Override
    public String test(String name) {
        return "hello" + name;
    }
}
