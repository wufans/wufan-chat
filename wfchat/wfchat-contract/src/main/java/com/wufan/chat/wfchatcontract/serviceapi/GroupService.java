package com.wufan.chat.wfchatcontract.serviceapi;

import com.wufan.chat.wfchatcommon.model.Message;
import com.wufan.chat.wfchatcommon.model.NewGroup;

import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/12 16:21
 */
public interface GroupService {
    /**
     * 新建 group, 实际上是新建一个 FriendRequest 和 User
     * @param newGroup
     * @param owner
     * @return
     */
    int newGroup(String owner, NewGroup newGroup);

    /**
     * 删除一个group，实际上是删除对应的User, Message, FriendRequest
     * @param groupname
     * @return
     */
    int deleteGroup(String groupname);

    /**
     * 加入一个群聊
     * @param username
     * @param groupname
     * @return
     */
    int joinGroup(String username, String groupname);


    List<Message> getGroupMessageByName(String groupname, Integer page, Integer pagNum);

    int exitGroup(String username, String groupname);
}
