package com.wufan.chat.wfchatcontract.serviceapi;

import com.wufan.chat.wfchatcommon.model.User;

/**
 * @Author: wufan
 * @Date: 2019/7/4 19:40
 * 用户服务接口定义
 */
public interface UserService {
    /**
     * 验证用户是否重复
     * @param username
     * @return
     */
    int varifyName(String username);

//    /**
//     * 用户登录
//     * @param name
//     * @param password
//     * @return
//     */
//    int logIn(String name, String password);

    /**
     * 用户注册
     * @param username
     * @param password
     * @param email
     * @param sex
     * @return
     */
    int insertUser(String username,String password,String email,Integer sex);

    /**
     * 更新密码
     * @param username
     * @param new_password
     */
    void updatePass(String username, String new_password);

    /**
     * 注销
     * @param username
     */
    void logOut(String username);

    /**
     * 根据用户名查找用户（及用户详细信息）
     * @param username
     * @return
     */
    User findUserByName(String username);

//    public Integer isFriendOrNot(String fromusername,String tousername);

    void sendVarifyEmail(String toEmail, String tile, String content);

}
