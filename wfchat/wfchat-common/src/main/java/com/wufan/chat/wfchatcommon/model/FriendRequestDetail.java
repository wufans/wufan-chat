package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wufan
 * @Date: 2019/7/10 11:03
 */
public class FriendRequestDetail implements Serializable {
    private static final long serialVersionUID = 7016603091861318664L;
    private Long requestid;
    private String fromusername;
    private String tousername;
    private Date requesttime;
    /**
     * 0:未处理； 1：已处理
     */
    private Integer isread;
    /**
     * 0:拒绝； 1：接受
     */
    private Integer isaccept;

    private Long latestmessageid;

    private User user;

    public FriendRequestDetail() {
    }
    public FriendRequestDetail(FriendRequest friendRequest){
        this.requestid = friendRequest.getRequestid();
        this.fromusername = friendRequest.getFromusername();
        this.tousername = friendRequest.getTousername();
        this.requesttime = friendRequest.getRequesttime();
        this.isread = friendRequest.getIsread();
        this.isaccept = friendRequest.getIsaccept();
        this.latestmessageid = friendRequest.getLatestmessageid();
    }

    public FriendRequestDetail(Long requestid, String fromusername, String tousername, Date requesttime, Integer isread, Integer isaccept, Long latestmessageid) {
        this.requestid = requestid;
        this.fromusername = fromusername;
        this.tousername = tousername;
        this.requesttime = requesttime;
        this.isread = isread;
        this.isaccept = isaccept;
        this.latestmessageid = latestmessageid;
    }

    public Long getRequestid() {
        return requestid;
    }

    public void setRequestid(Long requestid) {
        this.requestid = requestid;
    }

    public String getFromusername() {
        return fromusername;
    }

    public void setFromusername(String fromusername) {
        this.fromusername = fromusername;
    }

    public String getTousername() {
        return tousername;
    }

    public void setTousername(String tousername) {
        this.tousername = tousername;
    }

    public Date getRequesttime() {
        return requesttime;
    }

    public void setRequesttime(Date requesttime) {
        this.requesttime = requesttime;
    }

    public Integer getIsread() {
        return isread;
    }

    public void setIsread(Integer isread) {
        this.isread = isread;
    }

    public Integer getIsaccept() {
        return isaccept;
    }

    public void setIsaccept(Integer isaccept) {
        this.isaccept = isaccept;
    }

    public Long getLatestmessageid() {
        return latestmessageid;
    }

    public void setLatestmessageid(Long latestmessageid) {
        this.latestmessageid = latestmessageid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
