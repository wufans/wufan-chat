package com.wufan.chat.wfchattestclient.serverservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wufan.chat.wfchatcontract.serviceapi.ChatService;
import com.wufan.chat.wfchatcontract.serviceapi.FriendService;
import com.wufan.chat.wfchatcontract.serviceapi.GroupService;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @Author: wufan
 * @Date: 2019/7/4 20:05
 */
@Service
public class ServerBeanService {
//    @Reference(version = "1.0.0")
    private UserService userService;
    @Reference(version = "1.0.0")
    private FriendService friendService;
    @Reference(version = "1.0.0")
    private ChatService chatService;
    @Reference(version = "1.0.0")
    private GroupService groupService;

    @Reference(version = "1.0.0")
    public void getUserService(UserService userService){
        this.userService = userService;
    }


    @Bean
    UserService getUserService(){
        return this.userService;
    }

    @Bean
    FriendService getFriendService(){
        return this.friendService;
    }

    @Bean
    ChatService getChatService(){
        return this.chatService;
    }

    @Bean
    GroupService getGroupService(){
        return this.groupService;
    }

}
