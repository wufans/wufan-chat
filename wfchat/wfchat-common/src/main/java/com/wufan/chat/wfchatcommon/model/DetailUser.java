package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;

/**
 * @Author: wufan
 * @Date: 2019/7/5 17:41
 * 返回的用户详情
 */
public class DetailUser implements Serializable {
    private static final long serialVersionUID = 3691654009379439197L;
    private Long id;
    private String username;
    private String email;
    private String profile;
    private Integer sex;
    private Integer isfriend;

    public Integer getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(Integer isfriend) {
        this.isfriend = isfriend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
    public DetailUser(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profile = user.getProfile();
        this.sex = user.getSex();
    }
}
