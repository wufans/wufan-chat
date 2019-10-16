package com.wufan.chat.wfchatcontract.serviceapi;

import com.wufan.chat.wfchatcommon.model.User;

/**
 * @Author: wufan
 * @Date: 2019/7/14 12:54
 */
public interface RedisService {

    //获取user
    User getUserByName(String username);

    //修改user
    int deleteUser(String username);

    //插入user
    int insertUser(User user);
}
