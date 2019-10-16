package com.wufan.chat.wfchattestclient.servercontroller;

import com.wufan.chat.wfchatcommon.model.*;
import com.wufan.chat.wfchatcommon.rest.RestResponse;
import com.wufan.chat.wfchatcontract.serviceapi.ChatService;
import com.wufan.chat.wfchatcontract.serviceapi.FriendService;
import com.wufan.chat.wfchatcontract.serviceapi.GroupService;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/7 15:16
 */
@Controller
public class ChatServerController {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //发送消息请求
    @MessageMapping("/chat/sendchat")
    public void sendMessage(Principal principal, Message message){
        message.setMessage(message.getMessage().trim());

        String fromusername = principal.getName();
        String tousername = message.getTousername();
        String messagec = message.getMessage();//获取消息内容
        if(message.getMessage().equals("") || message.getMessage() == null){
            System.err.println(fromusername+" 发送了一条无效消息！");
            return;
        }
        Message newmessage = chatService.insertMessage(fromusername, tousername, messagec);

        if(newmessage != null){
            User userByName = userService.findUserByName(tousername);
            if(userByName.getSex() == 10){
                //如果发送的是群聊
                System.err.println(fromusername+" 发送了一条群组消息给 "+tousername+" : "+messagec);
                sendMessagetoGroup(fromusername, tousername);
            }else {
                //如果发送的聊天信息
                System.err.println(fromusername+" 发送了一条信息给用户 "+tousername+" : "+messagec);
                sendMessagetoUser(fromusername, tousername);
            }
        }
    }
    private void sendMessagetoUser(String fromusername, String tousername){
        //更新两个用户的20条聊天框
        flushUserChat(fromusername, tousername);
        flushUserChat(tousername,fromusername);
        //发送方的消息设置为已读，接收方需要发送已读请求
        readMessages(fromusername,tousername);
        //更新最近会话列表
        updateRecent(tousername);
        updateRecent(fromusername);

    }
    //推送 fromusername 发给 tousername 的历史记录给用户 fromusername
    private void flushUserChat(String fromusername, String tousername){
        List<Message> messages = chatService.getMessageByName(fromusername, tousername, 0, 20);
        RestResponse<List<DetailMessage>> restResponse = new RestResponse<>();
        restResponse.setMsg("new");
        restResponse.setData(changeMessageToDetail(fromusername,messages));
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/chathistory/"+tousername,restResponse);
        System.out.println("推送 "+fromusername+" 和 "+tousername+" 的聊天记录！");
    }
    private void sendMessagetoGroup(String fromusername, String groupname){
        readMessages(fromusername,groupname);
        //推送最近会话给所有的members
        List<FriendRequest> friendList = friendService.getMemberList(groupname);
        for(FriendRequest friendRequest:friendList){
            String membername = friendRequest.getFromusername();
            flushGroupChat(membername,groupname);
            updateRecent(membername);
        }

    }

    //推送group的所有订阅members
    private void flushGroupChat(String fromusername, String groupname) {
        List<Message> messages = groupService.getGroupMessageByName(groupname,0,20);
        List<DetailMessage> detailMessages = changeMessageToDetail(fromusername, messages);
        RestResponse<List<DetailMessage>> restResponse = new RestResponse<>();
        restResponse.setMsg("new").setData(detailMessages);
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/chathistory/"+groupname,restResponse);
//        messagingTemplate.convertAndSend("/topic/chathistory/"+groupname,restResponse);
        System.out.println("推送了群 "+groupname+" 的新消息！");
    }

    private List<DetailMessage> changeMessageToDetail(String fromusername, List<Message> messages) {
        List<DetailMessage> detailMessages = new ArrayList<>();
        for(Message message:messages){
            DetailMessage detailMessage = new DetailMessage(message);
            if(!message.getFromusername().equals(fromusername)){
                detailMessage.setIssendbyme("others");
            }
            User user = userService.findUserByName(message.getFromusername());
            if(user.getSex() == 10){
                System.err.println("聊天数据库出错！");
                return null;
            }
            detailMessage.setProfile(user.getProfile());
            detailMessages.add(detailMessage);
        }
        return detailMessages;
    }


    //分页查看聊天记录
    @MessageMapping("/chat/gethistorybypage")
    public void getHistoryByPage(Principal principal, ChatHistoryPage chatHistoryPage){
        String fromusername = principal.getName();
        String tousername = chatHistoryPage.getTousername();
        List<DetailMessage> detailMessages = getMessagesAndUserInfo(fromusername,chatHistoryPage);
        RestResponse<List<DetailMessage>> restResponse = new RestResponse<>();
        restResponse.setMsg("history");
        restResponse.setData(detailMessages);
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/chathistory/"+tousername,restResponse);
        System.out.println("历史消息分页查询："+fromusername+"查询和"+tousername+"的聊天记录！");
    }



    //点击用户头像，开始进入会话，并查询一页用户的聊天记录
    @MessageMapping("/chat/beginchat")
    public void beginChat(Principal principal, ChatHistoryPage chatHistoryPage){
        String fromusername = principal.getName();
        String tousername = chatHistoryPage.getTousername();
        List<DetailMessage> detailMessages = getMessagesAndUserInfo(fromusername,chatHistoryPage);
        //查询消息后，发送方消息变为已读
        readMessages(fromusername,tousername);
        //更新fromusername的聊天框
        RestResponse<List<DetailMessage>> restResponse = new RestResponse<>();
        restResponse.setMsg("new");
        restResponse.setData(detailMessages);
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/chathistory/"+tousername,restResponse);
        updateRecentAndAddFromuser(fromusername,tousername);
        System.out.println(fromusername+" 切换到了和 "+tousername+" 的聊天！");
    }

    //获取最近会话 + 对应的未读消息数量
    @MessageMapping("/chat/recentchat")
    public void getRecentChat(Principal principal){
        String fromusername = principal.getName();
        updateRecent(fromusername);
    }

    @MessageMapping("/chat/readmessages")
    private void readMessages(Principal principal,String tousername){
        String fromusername = principal.getName();
        readMessages(fromusername,tousername);
        updateRecent(fromusername);
    }
    //发起会话的时候根据是否已经包含了tousername，更新fromusername的最近会话列表
    private void updateRecentAndAddFromuser(String fromusername, String tousername){
//        List<RecentChat> recentChats = chatService.getRecentChat(fromusername);
        List<RecentChat> recentChats = chatService.getRecentChatAndGroup(fromusername);
        RestResponse<List<RecentChat>> restResponse = new RestResponse<>();
        Integer unReadMessagesNumber = 0;
        Boolean isIncludeTousername = false;
        for(RecentChat recentChat:recentChats){
            unReadMessagesNumber += recentChat.getUnreadnumber();
            if(recentChat.getUsername().equals(tousername)){
                isIncludeTousername = true;
            }
        }
        if(!isIncludeTousername){
            RecentChat recentChat = new RecentChat();
            User user = userService.findUserByName(tousername);
            recentChat.setProfile(user.getProfile());
            recentChat.setUsername(user.getUsername());
            recentChats.add(0,recentChat);
            recentChat.setContent("");
            System.out.println(fromusername+" 发起了与 "+tousername+" 的会话！");
        }
        restResponse.setCode(unReadMessagesNumber);

        restResponse.setData(recentChats);
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/recentchat",restResponse);
        System.out.println(fromusername+" 准备发消息并更新了最近会话列表！");

    }
    //根据名称更新最近会话列表
    private void updateRecent(String username){
        List<RecentChat> recentChats = chatService.getRecentChatAndGroup(username);
        Integer unReadMessagesNumber = 0;
        RestResponse<List<RecentChat>> restResponse = new RestResponse<>();
        for(RecentChat recentChat:recentChats){
            unReadMessagesNumber += recentChat.getUnreadnumber();
        }
        restResponse.setData(recentChats);
        restResponse.setCode(unReadMessagesNumber);
        messagingTemplate.convertAndSendToUser(username,"/topic/recentchat",restResponse);
//        logger.info(fromusername+" 查询了最近会话列表！");
        System.out.println(username+" 更新了最近会话列表！");
    }


    // 消息已读
    private void readMessages(String fromusername,String tousername){
        Integer i = chatService.setMessagesRead(fromusername, tousername);
        System.out.println(fromusername+" 读取了 "+tousername+" 发来的未读消息！");
    }

    //分页查找聊天记录，并附带是否是自己发送的标示
    private List<DetailMessage> getMessagesAndUserInfo(String fromusername, ChatHistoryPage chatHistoryPage){
        //判断用户是否是群组
        User user = userService.findUserByName(chatHistoryPage.getTousername());
        if(user == null){
            return new ArrayList<>();
        }
        List<Message> messages;
        if(user.getSex() == 10){
            messages = groupService.getGroupMessageByName(chatHistoryPage.getTousername(), chatHistoryPage.getPage(), chatHistoryPage.getPagNum());
        }else{
            messages = chatService.getMessageByName(fromusername, chatHistoryPage.getTousername(), chatHistoryPage.getPage(), chatHistoryPage.getPagNum());
        }
        return changeMessageToDetail(fromusername,messages);

    }

    @RequestMapping(value = "/testchat", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<Object> test(Principal principal){
//        Message message = new Message("hua","testgroup",new Date(),"test group message",1);
//        sendMessage(principal,message);

        return new RestResponse<>().setData(chatService.getRecentChatAndGroup("lipipeng"));
//        return null;
    }

}
