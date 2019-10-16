package com.wufan.chat.wfchatcommon.model;


import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wufan
 * @Date: 2019/7/5 19:15
 */
public class FriendRequest implements Serializable {
    private static final long serialVersionUID = -6635990733801622151L;
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
    /**
     * 群组用户的未读消息数
     */
    private Long latestmessageid;



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



    //收发的消息类，必须存在"无参的默认构造函数"，否则topic订阅会出问题，而且代码不报错！
    public FriendRequest() {
    }

    public FriendRequest(Long requestid, Integer isaccept) {
        this.requestid = requestid;
        this.isaccept = isaccept;
    }

    public FriendRequest(String fromusername, String tousername, Date requesttime, Integer isread, Integer isaccept, Long latestmessageid) {
        this.fromusername = fromusername;
        this.tousername = tousername;
        this.requesttime = requesttime;
        this.isread = isread;
        this.isaccept = isaccept;
        this.latestmessageid = latestmessageid;
    }
}
