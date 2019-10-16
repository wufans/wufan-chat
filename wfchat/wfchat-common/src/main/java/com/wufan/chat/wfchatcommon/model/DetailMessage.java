package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;

/**
 * @Author: wufan
 * @Date: 2019/7/12 20:29
 */
public class DetailMessage implements Serializable {
    private static final long serialVersionUID = -914326617367979791L;
    private Long messageid;
    private String fromusername;
    private String tousername;
    private String message;
    private String profile;
    private String issendbyme = "myself"; //"others","myself"

    public DetailMessage() {
    }

    public DetailMessage(Message oldmessage) {
        this.messageid = oldmessage.getMessageid();
        this.fromusername = oldmessage.getFromusername();
        this.tousername = oldmessage.getTousername();
        this.message = oldmessage.getMessage();
    }


    public DetailMessage(Long messageid, String fromusername, String tousername, String message, String profile, String issendbyme) {
        this.messageid = messageid;
        this.fromusername = fromusername;
        this.tousername = tousername;
        this.message = message;
        this.profile = profile;
        this.issendbyme = issendbyme;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getIssendbyme() {
        return issendbyme;
    }

    public void setIssendbyme(String issendbyme) {
        this.issendbyme = issendbyme;
    }
}
