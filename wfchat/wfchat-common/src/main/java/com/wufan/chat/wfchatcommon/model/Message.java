package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wufan
 * @Date: 2019/7/4 22:24
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -294724576340994159L;
    private Long messageid;
    private String fromusername;
    private String tousername;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Date sendtime;

    /**
     * 0:未读； 1：已读
     */
    private Integer isread;


    //收发的消息类，必须存在"无参的默认构造函数"，否则topic订阅会出问题，而且代码不报错！
    public Message() {
    }

    public Message(String fromusername, String tousername, Date sendtime, String message, Integer isread) {
        this.fromusername = fromusername;
        this.tousername = tousername;
        this.sendtime = sendtime;
        this.message = message;
        this.isread = isread;
    }

    public Long getMessageid() {
        return messageid;
    }

    public void setMessageid(Long messageid) {
        this.messageid = messageid;
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

    public Date getSendtime() {
        return sendtime;
    }

    public void setSendtime(Date sendtime) {
        this.sendtime = sendtime;
    }


    public Integer getIsread() {
        return isread;
    }

    public void setIsread(Integer isread) {
        this.isread = isread;
    }
}
