package com.wufan.chat.wfchattestclient.servercontroller;

import com.wufan.chat.wfchatcontract.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @Author: wufan
 * @Date: 2019/7/4 18:24
 */
@Deprecated
@Controller
public class TestServerController {
    private static final Logger logger = LoggerFactory.getLogger(TestServerController.class);

    @Autowired
    TestService testService;

    @RequestMapping(value = "/test",method = RequestMethod.GET)//, produces = "application/json; charset=utf-8")
    public @ResponseBody String test(){
        return testService.test(" test");
    }

}
