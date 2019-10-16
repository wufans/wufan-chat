package com.wufan.chat.wfchattestclient.servercontroller;

import com.wufan.chat.wfchatcommon.model.FriendRequest;
import com.wufan.chat.wfchatcommon.model.FriendRequestDetail;
import com.wufan.chat.wfchatcommon.model.RecentChat;
import com.wufan.chat.wfchatcommon.rest.RestResponse;
import com.wufan.chat.wfchatcontract.serviceapi.ChatService;
import com.wufan.chat.wfchatcontract.serviceapi.FriendService;
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
import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/7 14:33
 */
@Controller
public class FriendServerController {
    private static final Logger logger = LoggerFactory.getLogger(FriendServerController.class);

    @Autowired
    private FriendService friendService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    //获取好友列表
    @MessageMapping("/friend/friendlist")
    public void getFriendList(Principal principal){
        String username = principal.getName();
        List<FriendRequestDetail> friendList = friendService.getFriendList(username);
        RestResponse<List<FriendRequestDetail>> restResponse = new RestResponse<>();
        restResponse.setData(friendList).setMsg("好友列表");
        messagingTemplate.convertAndSendToUser(username,"/topic/friendlist",restResponse);
        System.out.println(username+" 查询了好友列表！");
    }


    //获取发送的好友请求列表
    @MessageMapping("/friend/sendreqlist")
    public void getFriendSendRequestList(Principal principal){
        String fromusername = principal.getName();
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/sendreqlist",getSendRequestResponse(fromusername));
//        logger.info(fromusername+" 查询了发送的好友请求列表！");
        System.out.println(fromusername+" 查询了发送的好友请求列表！");
    }

    //获取收到的好友请求列表
    @MessageMapping("/friend/receivereqlist")
    public void getFriendReceiveRequestList(Principal principal){
        String tousername = principal.getName();
        messagingTemplate.convertAndSendToUser(tousername, "/topic/receivereqlist",getReceivedRequestResponse(tousername));
//        logger.info(tousername+" 查询了收到的好友请求列表！");
        System.out.println(tousername+" 查询了收到的好友请求列表！");
    }


    //处理别人发来的好友请求
    @MessageMapping("/friend/replyrequest")
    public void getReplyFriendRequest(Principal principal, FriendRequest friendRequest){

        Long requestid = friendRequest.getRequestid();
        Integer isaccept = friendRequest.getIsaccept();
        String tousername = principal.getName();
        String fromusername = friendRequest.getFromusername();

        Integer resultcode = friendService.replyFriendRequest(requestid, isaccept);
        //如果接受了好友请求，需要利用websocket更新好友列表
        if(resultcode == 400){
            System.err.println("不能重复处理好友请求！");
            return;
        }if(resultcode == 300){
            System.err.println("数据库出错！");
            return;
        }

        if(isaccept == 1 && fromusername!=null && tousername != null){
            //更新好友列表和好友请求列表
            updateRequstAndFriend(fromusername, tousername);
//            logger.info(fromusername+String.format(" %s了 ",isaccept==1?"接受":"拒绝")+tousername+" 发来的好友请求！");
        }else{
            updateRequstAndFriend(fromusername, tousername);
        }
        System.err.println(tousername+String.format(" 成功%s了 ",isaccept==1?"接受":"拒绝")+fromusername+" 发来的好友请求！");

    }

    //发送好友请求
    @MessageMapping("/friend/sendrequest")
    public void sendFriendRequest(Principal principal, String tousername){
        String fromusername = principal.getName();
        int resultcode = friendService.sendFriendRequest(fromusername,tousername);
        if(resultcode == 200){
            messagingTemplate.convertAndSendToUser(fromusername,"/topic/sendreqlist",
                    getReceivedRequestResponse(fromusername));

            messagingTemplate.convertAndSendToUser(tousername,"/topic/receivereqlist",
                    getReceivedRequestResponse(tousername));
        }
        System.err.println(fromusername+" 给 "+tousername+"发送了好友请求，结果："+resultcode
                +"\n(200：发送成功 300：已经存在该好友 400：用户不存在 500: 添加了自己 600：重复的申请 700：数据库出错)");
    }



    //删除好友
    @MessageMapping("/friend/deletefriend")
    public void sendFriendRequest(Principal principal, FriendRequest friendRequest){
        Long requestid = friendRequest.getRequestid();
        String tousername =friendRequest.getTousername();
        String fromusername = friendRequest.getFromusername();

        String myname = principal.getName();
        String friend = myname.equals(fromusername)?tousername:fromusername;
        int resultcode = friendService.deleteFriend(requestid);
        if(resultcode > 0){
            //删除成功，socket更新两个好友申请列表和好友列表、最近会话列表
            updateRequstAndFriend(myname, friend);
            updateRecent(myname);
            updateRecent(friend);
        }
//        logger.error(fromusername+"删除了好友："+tousername);
        System.err.println(myname+"删除了好友："+friend);

    }
    //根据名称更新两个会话列表
    private void updateRecent(String username){
        List<RecentChat> recentChats = chatService.getRecentChat(username);
        RestResponse<List<RecentChat>> restResponse = new RestResponse<>();

        restResponse.setData(recentChats);
        Integer unReadMessagesNumber = 0;

        for(RecentChat recentChat:recentChats){
            unReadMessagesNumber = recentChat.getUnreadnumber()+ unReadMessagesNumber;
        }
        restResponse.setCode(unReadMessagesNumber);
        messagingTemplate.convertAndSendToUser(username,"/topic/recentchat",restResponse);

        System.out.println(username+" 更新了最近会话列表！");
    }

    //自定义好友请求列表返回体
    private RestResponse<List<FriendRequestDetail>> getReceivedRequestResponse(String username){
        List<FriendRequestDetail> friendReceiveRequestList = friendService.getFriendReceiveRequestList(username);
        RestResponse<List<FriendRequestDetail>> restResponse = new RestResponse<>();
        restResponse.setData(friendReceiveRequestList);
        int nohandlerequest = 0;
        for(FriendRequestDetail friendRequestDetail:friendReceiveRequestList){
            if(friendRequestDetail.getIsread() == 0){
                nohandlerequest = nohandlerequest + 1;
            }
        }
        restResponse.setCode(nohandlerequest);
        return restResponse;
    }
    //自定义发送请求列表返回体
    private RestResponse<List<FriendRequestDetail>> getSendRequestResponse(String username){
        List<FriendRequestDetail> friendSendRequestList = friendService.getFriendSendRequestList(username);
        RestResponse<List<FriendRequestDetail>> restSendResponse = new RestResponse<>();
        restSendResponse.setData(friendSendRequestList);
        return restSendResponse;
    }

    //自定义好友列表返回体
    private RestResponse<List<FriendRequestDetail>> getTrueFriendResponse(String username){
        List<FriendRequestDetail> friendList = friendService.getFriendList(username);
        RestResponse<List<FriendRequestDetail>> restResponse = new RestResponse<>();
        restResponse.setData(friendList).setMsg("好友列表");
        return restResponse;
    }

    //更新好友请求列表和好友列表
    private void updateRequstAndFriend(String fromusername, String tousername){
        //更新两个好友请求列表
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/sendreqlist",getSendRequestResponse(fromusername));
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/receivereqlist", getReceivedRequestResponse(fromusername));
        messagingTemplate.convertAndSendToUser(tousername,"/topic/sendreqlist",getSendRequestResponse(tousername));
        messagingTemplate.convertAndSendToUser(tousername,"/topic/receivereqlist", getReceivedRequestResponse(tousername));
        //更新两个好友列表
        messagingTemplate.convertAndSendToUser(fromusername,"/topic/friendlist",getTrueFriendResponse(fromusername));
        messagingTemplate.convertAndSendToUser(tousername,"/topic/friendlist",getTrueFriendResponse(tousername));
    }

    //测试方法
    @RequestMapping(value = "/testfriend", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<List<FriendRequestDetail>> test(Principal principal) {
        List<FriendRequestDetail> friendSendRequestDetailList = friendService.getFriendSendRequestList(principal.getName());
        return new RestResponse<List<FriendRequestDetail>>().setData(friendSendRequestDetailList);

    }
}
