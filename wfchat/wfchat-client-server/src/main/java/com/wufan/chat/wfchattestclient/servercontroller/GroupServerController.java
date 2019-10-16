package com.wufan.chat.wfchattestclient.servercontroller;

import com.wufan.chat.wfchatcommon.model.*;
import com.wufan.chat.wfchatcommon.rest.RestResponse;
import com.wufan.chat.wfchatcontract.serviceapi.ChatService;
import com.wufan.chat.wfchatcontract.serviceapi.FriendService;
import com.wufan.chat.wfchatcontract.serviceapi.GroupService;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
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
 * @Date: 2019/7/12 14:11
 */

@Controller
public class GroupServerController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //获取群列表
    @MessageMapping("/group/grouplist")
    public void getGroupList(Principal principal){
        String username = principal.getName();
        List<FriendRequestDetail> friendList = friendService.getGroupList(username);
        RestResponse<List<FriendRequestDetail>> restResponse = new RestResponse<>();
        restResponse.setData(friendList).setMsg("群列表");
        messagingTemplate.convertAndSendToUser(username,"/topic/grouplist",restResponse);
        System.out.println(username+" 查询了群列表！");
    }

    //发起群聊
    @MessageMapping("/group/newgroup")
    public void newGroup(Principal principal, NewGroup newGroup){
        String owername = principal.getName();
        //判断群聊人数是否是大于2 (members是否大于1，如果小于1，则不处理)
        if(newGroup.getMembers().size() <= 1){
            System.err.println(owername+" 因为申请的用户数量不够，创建群聊失败！");
            return;
        }
        //新建 Group
        Integer resultcode = groupService.newGroup(owername, newGroup);
        System.err.println(owername+" 创建群聊"+(resultcode == 200?"成功!":"失败!"));
        if(resultcode == 400){
            return;
        }
        //更新所有group列表，
        updateGroupList(owername);
        for(String membername: newGroup.getMembers()){
            updateGroupList(membername);
        }
        //同时更新owername的最近会话列表，插入这个RencentChat对象
        updateRecentAndAddFromuser(owername, newGroup.getGroupname());
    }

    //解散群聊
    @MessageMapping("/group/deletegroup")
    public void deleteGroup(Principal principal, String groupname){
        //判断发送请求的人是否是管理员
        String username = principal.getName();
        User user = userService.findUserByName(groupname);
        if(!username.equals(user.getEmail())){
            System.err.printf(username+" 无权解散群聊!");
            return;
        }
        updateGroupList(username);
        updateRecent(username);
        List<FriendRequest> friendList = friendService.getMemberList(groupname);
        groupService.deleteGroup(groupname);
        //更新成员好友列表和会话列表
        for(FriendRequest friendRequest: friendList){
            String membername = friendRequest.getFromusername();
            updateGroupList(membername);
            updateRecent(membername);
        }
        System.err.printf(username+" 成功解散群聊!");
    }


    //加入群聊
    @MessageMapping("/group/joingroup")
    public void joinGroup(Principal principal, String groupname){
        String username = principal.getName();
        int resultcode = groupService.joinGroup(username, groupname);
        if(resultcode == 200){
            System.err.println(username+" 加入群聊 "+groupname+" 成功！");
        }else{
            System.err.println(username+" 加入群聊 "+groupname+" 失败！");
            return;
        }
        //需要更新好友列表
        updateGroupList(username);
        updateRecent(username);
        //更新最近会话列表

    }



    // 退出群聊
    @MessageMapping("/group/exitgroup")
    public void exitGroup(Principal principal, String groupname){
        String username = principal.getName();
        User group = userService.findUserByName(groupname);
        if(group.getEmail().equals(username)){
            List<FriendRequest> friendRequestList = friendService.getMemberList(groupname);
            deleteGroup(principal, groupname);
            for(FriendRequest friendRequest:friendRequestList){
                updateRecent(friendRequest.getFromusername());
                updateGroupList(friendRequest.getFromusername());
            }
            System.err.println(username+" 删除了群聊 "+groupname);
            return;
        }
        int result = groupService.exitGroup(username, groupname);
        updateRecent(username);
        updateGroupList(username);
        System.err.println(username+" 退出群聊 "+groupname+ (result == 200?" 成功!":" 失败!"));
    }

    //todo 管理员转让群
    public void changeGroup(Principal principal, String groupname,String username){
        //把groupname转让给username

        //修改groupname对应的email字段
    }

    //根据名称更新最近会话列表
    private void updateRecent(String username){
        List<RecentChat> recentChats = chatService.getRecentChatAndGroup(username);
        RestResponse<List<RecentChat>> restResponse = new RestResponse<>();
        Integer unReadMessagesNumber = 0;
        for(RecentChat recentChat:recentChats){
            unReadMessagesNumber += recentChat.getUnreadnumber();
        }
        restResponse.setCode(unReadMessagesNumber).setData(recentChats);
        messagingTemplate.convertAndSendToUser(username,"/topic/recentchat",restResponse);
//        logger.info(fromusername+" 查询了最近会话列表！");
        System.out.println(username+" 更新了最近会话列表！");
    }

    //更新群组成员好友列表
    private void updateGroupList(String membername){
        //获取成员信息
        List<FriendRequestDetail> friendList = friendService.getGroupList(membername);
        RestResponse<List<FriendRequestDetail>> restResponse = new RestResponse<>();
        restResponse.setData(friendList);
        restResponse.setMsg("更新群组信息");
        //更新群组成员好友列表
        messagingTemplate.convertAndSendToUser(membername,"/topic/grouplist",restResponse);
        System.out.println("更新了 "+membername+" 的好友列表！");
    }



    //发起会话的时候根据是否已经包含了groupname，更新username的最近会话列表
    private void updateRecentAndAddFromuser(String membername, String groupname){
        List<RecentChat> recentChats = chatService.getRecentChat(membername);
        RestResponse<List<RecentChat>> restResponse = new RestResponse<>();
        Boolean isIncludeTousername = false;
        Integer unReadMessagesNumber = 0;
        //确认是否需要新建一个窗口并置顶
        for(RecentChat recentChat:recentChats){
            unReadMessagesNumber += recentChat.getUnreadnumber();
            if(recentChat.getUsername().equals(groupname)){
                isIncludeTousername = true;
            }
        }
        if(!isIncludeTousername){
            RecentChat recentChat = new RecentChat();
            User user = userService.findUserByName(groupname);
            recentChat.setProfile(user.getProfile());
            recentChat.setUsername(user.getUsername());
            recentChat.setIsread(user.getSex());//对于群而言，这个数字为 10
            recentChat.setContent("");
            recentChats.add(0,recentChat);
            System.out.println(membername+" 发起了群聊消息！");
        }
        restResponse.setCode(unReadMessagesNumber);

        restResponse.setData(recentChats);
        messagingTemplate.convertAndSendToUser(membername,"/topic/recentchat",restResponse);
        System.out.println(membername+" 准备发消息并更新了最近会话列表！");

    }


    //测试方法
    @RequestMapping(value = "/testgroup", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<List<FriendRequestDetail>> test(Principal principal) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("wufan");
        objects.add("lipipeng");
        newGroup(principal,new NewGroup("testgroup",objects));
//        deleteGroup(principal, "testgroup");
        return null;
    }

}
