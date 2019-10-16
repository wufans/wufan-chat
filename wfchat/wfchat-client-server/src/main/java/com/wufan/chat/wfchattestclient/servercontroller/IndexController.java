package com.wufan.chat.wfchattestclient.servercontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: wufan
 * @Date: 2019/7/5 13:20
 */
@Deprecated
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("login")
    public String login(){
        return "login";
    }
    @GetMapping("chat")
    public String chat(){
        return "chat";
    }
    @GetMapping("friend")
    public String friend(){
        return "friend";
    }
}
