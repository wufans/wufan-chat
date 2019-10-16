package com.wufan.chat.wfchatcommon.model;
//
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @Author: wufan
 * @Date: 2019/7/4 17:52
 */
//
public class User implements UserDetails {
    private static final long serialVersionUID = -4605901264398854831L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private String profile;
    private Integer sex;

    public User() {
    }
    public User(String username, String password, String email, String profile, Integer sex) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profile = profile;
        this.sex = sex;
    }
    public User(Long id, String username, String password, String email, String profile, Integer sex) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profile = profile;
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile() {
        return profile;
    }

    public Integer getSex() {
        return sex;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
