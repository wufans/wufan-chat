package com.wufan.chat.wfchattestclient.serverservice;

import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @Author: wufan
 * @Date: 2019/7/5 15:23
 *
 */
@Deprecated
@Service
public class LogInService implements UserDetailsService {
    @Autowired
    UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByName(username);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        User u = new User();
        u.setUsername(user.getUsername());
        u.setPassword(user.getPassword());
        System.out.println(u.getUsername()+"--"+u.getPassword()+"-");
        return u;
    }
}
