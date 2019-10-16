package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;

/**
 * @Author: wufan
 * @Date: 2019/7/10 21:52
 */
public class ChatHistoryPage implements Serializable {
    private static final long serialVersionUID = -1868899955515489425L;
    private String tousername;
    private Integer page = 0;
    private Integer pagNum = 10;

    public ChatHistoryPage(String tousername, Integer page, Integer pagNum) {
        this.tousername = tousername;
        this.page = page;
        this.pagNum = pagNum;
    }

    public ChatHistoryPage() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTousername() {
        return tousername;
    }

    public void setTousername(String tousername) {
        this.tousername = tousername;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPagNum() {
        return pagNum;
    }

    public void setPagNum(Integer pagNum) {
        this.pagNum = pagNum;
    }
}
