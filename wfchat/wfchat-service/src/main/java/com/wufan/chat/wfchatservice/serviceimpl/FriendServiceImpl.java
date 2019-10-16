package com.wufan.chat.wfchatservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wufan.chat.wfchatcommon.model.FriendRequest;
import com.wufan.chat.wfchatcommon.model.FriendRequestDetail;
import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.FriendService;
import com.wufan.chat.wfchatcontract.serviceapi.RedisService;
import com.wufan.chat.wfchatrepository.mapper.FriendDao;
import com.wufan.chat.wfchatrepository.mapper.MessageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/5 19:42
 */

@Service(version = "1.0.0")
public class FriendServiceImpl implements FriendService {
    private static final Logger logger = LoggerFactory.getLogger(FriendServiceImpl.class);

    @Autowired
    private FriendDao friendDao;
//    @Autowired
//    private UserDao userDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RedisService redisService;


    //获取好友列表
    @Override
    public List<FriendRequestDetail> getFriendList(String fromusername) {
        List<FriendRequest> friendRequestList = friendDao.getTrueFriendByNama(fromusername);
        return addUserInfo(friendRequestList,fromusername);
    }

    //获取成员对应的群列表
    @Override
    public List<FriendRequestDetail> getGroupList(String username){
        List<FriendRequest> friendRequestList = friendDao.getUserGroup(username);
        return addUserInfo(friendRequestList,username);
    }
    //获取群对应的成员列表
    @Override
    public List<FriendRequest> getMemberList(String groupname){
        List<FriendRequest> friendRequestList = friendDao.getMemberList(groupname);
        return friendRequestList;
    }

    //获取发送的好友申请列表
    @Override
    public List<FriendRequestDetail> getFriendSendRequestList(String fromusername) {
        List<FriendRequest> friendSendRequestList = friendDao.getSendFriendRequestByName(fromusername);
        //去除已处理的请求
        List<FriendRequest> friendSendRequests = new ArrayList<>();
        for(FriendRequest f: friendSendRequestList){
            if(f.getIsread() == 1){
                continue;
            }
            friendSendRequests.add(f);
        }
        return addUserInfo(friendSendRequests, fromusername);
    }

    //获取收到的好友申请列表
    @Override
    public List<FriendRequestDetail> getFriendReceiveRequestList(String tousername) {
        //查看tousername收到的所有好友请求
        List<FriendRequest> friendReceiveRequestList = friendDao.getReceiveFriendRequestByName(tousername);
        //去除已处理的请求
        List<FriendRequest> friendRequests = new ArrayList<>();
        for(FriendRequest f: friendReceiveRequestList){
            if(f.getIsread() != 1){
                friendRequests.add(f);
            }
        }
        //添加发送好友请求的人的所有信息
        List<FriendRequestDetail> friendRequestDetailList = addUserInfo(friendRequests, tousername);
        return friendRequestDetailList;
    }

    //回复好友申请
    @Override
    public Integer replyFriendRequest(Long requestid, int isaccept) {
        //如果已经处理过了，返回400
        FriendRequest friendRequest = friendDao.isHandled(requestid);
        if(friendRequest == null || friendRequest.getIsread() == 1){
            return 400;
        }
        //更新数据库，设置为已处理
        Integer result = friendDao.replyFriendRequest(requestid, isaccept);
        return result != null ?200:300;
    }

    /**
     *
     * @param fromusername
     * @param tousername
     * @return 300好友存在 400 用户不存在 500 重复的申请 600 其他错误
     */
    @Override
    public int sendFriendRequest(String fromusername, String tousername) {
        if(fromusername.equals(tousername)){
            //不能添加自己为好友
            return 500;
        }
        //检查用户是否存在
//        User user = userDao.getByName(tousername);
        User user = redisService.getUserByName(tousername);
        if(user == null){
            return 400;
        }
        //检查好友是否重复
        List<FriendRequest> friendList = friendDao.getTrueFriendByNama(fromusername);
        for(FriendRequest friendRequest:friendList){
            if((friendRequest.getFromusername().equals(fromusername) && friendRequest.getTousername().equals(tousername))
                || (friendRequest.getFromusername().equals(tousername) && friendRequest.getTousername().equals(fromusername))){
                return 300;
            }
        }
        //检查是否存在未读重复好友申请
        List<FriendRequest> list = friendDao.getUnconfirmRequest(fromusername,tousername);
        if(list!=null && !list.isEmpty()){
            return 600;
        }
        Date date = new Date();
        FriendRequest friendRequest = new FriendRequest(fromusername,tousername,date,0,0,0L);
        int result = friendDao.insertFriendRequest(friendRequest);
        return result > 0? 200 : 700;
    }

    //删除好友信息：删除好友的申请关系和所有的历史聊天记录
    @Override
    public int deleteFriend(Long requestid) {
        FriendRequest friendRequest = friendDao.getNameByRequestId(requestid);
        int result = friendDao.deleteFriendRequest(requestid);
        messageDao.deleteMessages(friendRequest.getFromusername(), friendRequest.getTousername());
        return result;
    }

    @Override
    public Integer isFriendOrNot(String fromusername,String tousername){
        List<FriendRequest> requests = friendDao.isFriendOrNot(fromusername, tousername);

        if(requests != null && !requests.isEmpty()){

            return 1;
        }
        return 0;
    }

    //将FriendRequest添加个人信息变成FriendRequestDetail
    private List<FriendRequestDetail> addUserInfo(List<FriendRequest> friendRequests, String myname){
        List<FriendRequestDetail> friendList = new ArrayList<>();
        for(FriendRequest friendRequest: friendRequests){
            FriendRequestDetail friendRequestDetail = new FriendRequestDetail(friendRequest);
            String fromusername = friendRequest.getFromusername();
            String tousername = friendRequest.getTousername();
//            User toUserInfo = userDao.getByName(fromusername.equals(myname)?tousername:fromusername);
            User toUserInfo = redisService.getUserByName(fromusername.equals(myname)?tousername:fromusername);
            toUserInfo.setPassword("");
            friendRequestDetail.setUser(toUserInfo);
            friendList.add(friendRequestDetail);
        }
        // 按照名称首字母排序
        friendList.sort((f1,f2)->f1.getUser().getUsername().compareTo(f2.getUser().getUsername()));
        return friendList;
    }

}
