package com.wufan.chat.wfchatcontract.serviceapi;

import com.wufan.chat.wfchatcommon.model.Message;
import com.wufan.chat.wfchatcommon.model.RecentChat;

import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/4 19:40
 */
public interface ChatService {
    /**
     * 插入信息并更新
     * @param fromusername
     * @param tousername
     * @param message
     * @return
     */
    Message insertMessage(String fromusername,String tousername, String message);

    /**
     * 获取两个用户之间的所有聊天记录
     * @param fromusername
     * @param tousername
     * @return
     */
    List<Message> getMessageByName(String fromusername,String tousername);

    /**
     * 分页查询两个用户之间的聊天记录
     * @param fromusername
     * @param tousername
     * @param page
     * @return
     */
    List<Message> getMessageByName(String fromusername,String tousername, Integer page,Integer pagNum);


    /**
     * 获取两个用户之间的未读消息数
     * @param receiveusername
     * @param sendusername
     * @return
     */
    Integer getSomeoneUnreadMessage(String receiveusername, String sendusername);

    /**
     * 获取某个用户的最近聊天列表 + 未读消息数量
     * @param username
     * @return
     */
    List<RecentChat> getRecentChat(String username);


    /**
     * 获取某个用户的最近聊天列表 + 未读消息数量； 包括群组消息列表
     * @param username
     * @return
     */
    List<RecentChat> getRecentChatAndGroup(String username);


    /**
     * 将用户收到的所有聊天置为已读
     * @param fromusername
     * @param tousername
     * @return
     */
    Integer setMessagesRead(String fromusername, String tousername);
}
