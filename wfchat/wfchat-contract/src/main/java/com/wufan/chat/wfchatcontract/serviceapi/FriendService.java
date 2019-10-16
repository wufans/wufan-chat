package com.wufan.chat.wfchatcontract.serviceapi;

import com.wufan.chat.wfchatcommon.model.FriendRequest;
import com.wufan.chat.wfchatcommon.model.FriendRequestDetail;

import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/4 19:40
 */
public interface FriendService {
    /**
     * 获取好友列表
     * @param fromusername
     * @return
     */
    List<FriendRequestDetail> getFriendList(String fromusername);


    //获取群列表
    List<FriendRequestDetail> getGroupList(String username);

    //获取群对应的成员列表
    List<FriendRequest> getMemberList(String groupname);

    /**
     * 获取发送的好友请求列表
     * @param fromusername
     * @return
     */
    List<FriendRequestDetail> getFriendSendRequestList(String fromusername);

    /**
     * 获取收到的好友请求列表
     * @param tousername
     * @return
     */
    List<FriendRequestDetail> getFriendReceiveRequestList(String tousername);

    /**
     * 接受/拒绝 好友请求
     * @param requestid 处理的请求ID
     * @param isaccept 0：拒绝 ；1：接受
     * @return
     */
    Integer replyFriendRequest(Long requestid, int isaccept);

    /**
     * 发送好友请求
     * @param fromusername
     * @param tousername
     * @return 200：发送成功 300：已经存在该好友 400：用户名错误 500: 数据库错误
     */
    int sendFriendRequest(String fromusername, String tousername);

//    int readRequest(String tousername);

    /**
     * 删除好友
     * @param requestid
     * @return
     */
    int deleteFriend(Long requestid);

    Integer isFriendOrNot(String fromusername, String tousername);
}
