package com.wufan.chat.wfchatservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wufan.chat.wfchatcommon.model.FriendRequest;
import com.wufan.chat.wfchatcommon.model.Message;
import com.wufan.chat.wfchatcommon.model.RecentChat;
import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.ChatService;
import com.wufan.chat.wfchatcontract.serviceapi.RedisService;
import com.wufan.chat.wfchatrepository.mapper.FriendDao;
import com.wufan.chat.wfchatrepository.mapper.MessageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author: wufan
 * @Date: 2019/7/8 15:11
 */
@Service(version = "1.0.0")
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    @Autowired
    private MessageDao messageDao;
//    @Autowired
//    private UserDao userDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FriendDao friendDao;

    //添加一条消息
    @Override
    public Message insertMessage(String fromusername,String tousername, String message) {
        if(fromusername.equals(tousername) || message == null){
            return null;
        }
        Date date = new Date();
        Message newmessage = new Message(fromusername,tousername,date, message,0);
        User user = redisService.getUserByName(tousername);
        if(user == null){
            System.err.println("写入消息失败，没有这个用户！");
            return null;
        }
        if(user.getSex() == 10){
            //获取所有的用户数
            List<FriendRequest> trueFriendByNama = friendDao.getMemberList(tousername);
            //更新群聊未读数+1
            for(FriendRequest friendRequest:trueFriendByNama){
                friendDao.addGroupUnreadNumber(friendRequest.getFromusername(),tousername);
            }
            newmessage.setIsread(1);
            messageDao.insertMessage(newmessage);
        }else{
            messageDao.insertMessage(newmessage);
        }
        return newmessage;
    }

    //获取俩用户之间的所有聊天记录
    @Override
    public List<Message> getMessageByName(String fromusername, String tousername) {
        List<Message> allMessages = new ArrayList<>();
        if(fromusername == null || tousername == null){
            return allMessages;
        }
        if(fromusername.equals(tousername)){
            return allMessages;
        }
        allMessages = messageDao.getAllMessage(fromusername, tousername);
        return allMessages;
    }

    //分页获取俩个用户之间的聊天记录
    @Override
    public List<Message> getMessageByName(String fromusername, String tousername, Integer page,Integer pagNum) {
        List<Message> allMessagesBypage = new ArrayList<>();
        if(fromusername == null || tousername == null){
            return allMessagesBypage;
        }
        if(fromusername.equals(tousername)){
            return allMessagesBypage;
        }
        allMessagesBypage = messageDao.getMessagesByPage(fromusername,tousername,page*pagNum,pagNum);
        return allMessagesBypage;
    }
    //获取用户收到的未读消息数
    @Override
    public Integer getSomeoneUnreadMessage(String fromusername, String tousername) {
        if(fromusername == null || tousername == null){
            return 0;
        }
        if(fromusername.equals(tousername)){
            return 0;
        }
        List<Message> messages = messageDao.getUnreadMessageNumber(fromusername, tousername);
//        logger.info("用户收到的未读消息数量查询： 查询人："+fromusername+" -- 消息发送者："+tousername+" -- 未读消息数："+messages.size());
        return messages.size();
    }

    //得到某个用户最近的聊天列表 和 每个用户对应未读消息数
    @Override
    public List<RecentChat> getRecentChat(String fromusername) {
        //获取最近的聊天列表
        List<Message> messages =  messageDao.getRecentChatName(fromusername);
        List<RecentChat> recentChats = new ArrayList<>();
        if(fromusername == null || messages == null || messages.isEmpty()){
            return recentChats;
        }
        for(Message s:messages){
            // 插入用户头像
//            User user = userDao.getByName(s.getTousername());
            User user = redisService.getUserByName(s.getTousername());
            //排除群组消息， 因为这种方法找到的最近一条消息可能和fromusername无关
            if(user == null || user.getSex() == 10){
                continue;
            }
            // 插入最近消息内容、消息id、消息内容、消息发送时间、消息状态
            RecentChat recentChat = new RecentChat(s);
            recentChat.setProfile(user.getProfile());
            //未读消息数
            Integer number = getSomeoneUnreadMessage(fromusername,s.getFromusername());
            recentChat.setUnreadnumber(number);
            recentChats.add(recentChat);
            //消息是不是用户发送的
            String tousername = s.getTousername();
            recentChat.setIssendbyme(tousername.equals(fromusername)? 0 : 1);
        }
        return recentChats;
    }

    //获取包含了群组消息的最近会话列表
    @Override
    public List<RecentChat> getRecentChatAndGroup(String username) {

//        1. 取出用户的所有最近联系人
        List<RecentChat> recentChats = getRecentChat(username);
//        2. 从FriendRequest里面取出用户对应的所有Group
        List<FriendRequest> groupRequestList = friendDao.getUserGroup(username);
//        3. 根据Groupname取出最近联系Group的最近联系列表
//        List<Message> groupMessages = new ArrayList<>();
        if(username == null || groupRequestList == null || groupRequestList.isEmpty()){
            return recentChats;
        }
        for(FriendRequest friendRequest: groupRequestList){
            String groupname = friendRequest.getTousername();
//            User group = userDao.getByName(groupname);
            User group = redisService.getUserByName(groupname);
            FriendRequest f = friendDao.getGroupUnreadNumer(username, groupname);
            Message message = messageDao.getRecentMessageOfGroup(groupname);
            if(group == null || message == null){
                continue;
            }
            RecentChat recentChat = new RecentChat(message);
            recentChat.setUsername(groupname);
            recentChat.setProfile(group.getProfile());
            recentChat.setIsread(group.getSex());
            recentChat.setUnreadnumber(f.getLatestmessageid().intValue());
            recentChat.setIssendbyme(message.getFromusername().equals(username)?1:0);
            recentChats.add(recentChat);
        }
    //        4. 合并两个列表
        //noinspection ComparatorMethodParameterNotUsed
        if(recentChats!=null){
            recentChats.sort((recentChat1,recentChat2)-> recentChat1.getSendtime().after(recentChat2.getSendtime())?-1:1);
        }
        return recentChats;
    }


    //将用户发来的消息标记为已读
    @Override
    public Integer setMessagesRead(String fromusername, String tousername) {
        if(fromusername == null || tousername == null){
            return 0;
        }
        if(fromusername.equals(tousername)){
            return 0;
        }
        Integer resultnumber;
        //如果是聊天消息
//        User user = userDao.getByName(tousername);
        User user = redisService.getUserByName(tousername);
        if(user == null){
            System.err.println(fromusername+"读取消息失败：没有可读消息！");
            return 0;
        }
        if(user.getSex() == 10){
            //将群组消息设置为已读
            resultnumber = friendDao.setGroupUnreadNumber(fromusername,tousername);
        }else{
            resultnumber = messageDao.setMessagesRead(fromusername,tousername);
        }
        if(resultnumber == null){
            return 0;
        }
        return resultnumber;
    }


}
