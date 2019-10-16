package com.wufan.chat.wfchatservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wufan.chat.wfchatcommon.model.FriendRequest;
import com.wufan.chat.wfchatcommon.model.Message;
import com.wufan.chat.wfchatcommon.model.NewGroup;
import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.GroupService;
import com.wufan.chat.wfchatcontract.serviceapi.RedisService;
import com.wufan.chat.wfchatrepository.mapper.FriendDao;
import com.wufan.chat.wfchatrepository.mapper.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author: wufan
 * @Date: 2019/7/12 16:32
 */
@Service(version = "1.0.0")
public class GroupServiceImpl implements GroupService {
//    @Autowired
//    private UserDao userDao;
    @Autowired
    private FriendDao friendDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RedisService redisService;

    @Override
    public int newGroup(String owner, NewGroup newGroup) {
        String groupname = newGroup.getGroupname();
        //判断是否已经包含了重复的 groupname
//        User varifyuser = userDao.getByName(groupname);
        User varifyuser = redisService.getUserByName(groupname);
        if(varifyuser != null){
            return 400;
        }
        //去掉重复的邀请的用户名
        Set<String> members = new HashSet<>(newGroup.getMembers());
        String groupprofile = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1024504869,25874000&fm=26&gp=0.jpg";
        //新建 Group User, 为了避免用户用Group登录，password值为空
        User user = new User(groupname, "",owner, groupprofile, 10);
//        userDao.insertUser(user);
        redisService.insertUser(user);
        //对于每一个member，新建一条FriendRequest
        for(String member:members){
            FriendRequest friendRequest = new FriendRequest(member, groupname,new Date(), 10,1,0L);
            friendDao.insertFriendRequest(friendRequest);
        }
        //增加owner对应的FriendRequest
        FriendRequest friendRequest = new FriendRequest(owner, groupname,new Date(), 10,1,0L);
        friendDao.insertFriendRequest(friendRequest);
        return 200;

    }

    @Override
    public int deleteGroup(String groupname) {
        //删除用户
//        userDao.deleteUser(groupname);
        redisService.deleteUser(groupname);
        //删除所有的member
        Integer deleteNumber = friendDao.deleteByName(groupname);
        //删除所有的聊天信息
        messageDao.deleteGroupMessages(groupname);
        return deleteNumber != null ? 200 : 400;
    }

    @Override
    public int joinGroup(String username, String groupname) {
        FriendRequest friendRequest = new FriendRequest(username, groupname,new Date(), 10,1,0L);
        Integer resultcode = friendDao.insertFriendRequest(friendRequest);
        return resultcode != null? 200:400;
    }

    /**
     * 分页获取某个group的所有聊天记录
      * @param groupname
     * @param page
     * @param pagNum
     * @return
     */
    @Override
    public List<Message> getGroupMessageByName(String groupname, Integer page, Integer pagNum) {
        List<Message> allMessagesBypage = new ArrayList<>();
        if(groupname == null ){
            return allMessagesBypage;
        }
        allMessagesBypage = messageDao.getGroupMessagesByPage(groupname,page*pagNum,pagNum);
        return allMessagesBypage;
    }


    @Override
    public int exitGroup(String username, String groupname){
//        User user = redisService.getUserByName(groupname);
//        if(user.getEmail().equals(username)){
//            deleteGroup(groupname);
//        }
        Integer resultcode = friendDao.exitGroup(username,groupname);
        return resultcode == null ?400:200;
    }



}
