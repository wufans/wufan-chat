package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wufan
 * @Date: 2019/7/9 19:24
 */
public class RecentChat implements Serializable {
    private static final long serialVersionUID = -7734714555944412097L;

    private String username;
    private String profile;
    private String content;
    private Long messageid;
    private Integer unreadnumber;
    private Date sendtime;
    private Integer isread;
    private Integer issendbyme;

    public Integer getIssendbyme() {
        return issendbyme;
    }

    public void setIssendbyme(Integer issendbyme) {
        this.issendbyme = issendbyme;
    }

    public RecentChat() {
    }

    public RecentChat(Message message) {
        this.username = message.getFromusername();
        String content = message.getMessage();
        if(content.length()>10){
            this.content = content.substring(0,9)+"...";
        }else{
            this.content = content;
        }

        this.messageid = message.getMessageid();
        this.sendtime = message.getSendtime();
        this.isread = message.getIsread();
    }
    public RecentChat(String username, String profile, String content, Long messageid, Integer unreadnumber) {
        this.username = username;
        this.profile = profile;
        this.content = content;
        this.messageid = messageid;
        this.unreadnumber = unreadnumber;
    }

    public RecentChat(String username, String profile, String content, Long messageid, Integer unreadnumber, Date sendtime) {
        this.username = username;
        this.profile = profile;
        this.content = content;
        this.messageid = messageid;
        this.unreadnumber = unreadnumber;
        this.sendtime = sendtime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getMessageid() {
        return messageid;
    }

    public void setMessageid(Long messageid) {
        this.messageid = messageid;
    }

    public Integer getUnreadnumber() {
        return unreadnumber;
    }

    public void setUnreadnumber(Integer unreadnumber) {
        this.unreadnumber = unreadnumber;
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
