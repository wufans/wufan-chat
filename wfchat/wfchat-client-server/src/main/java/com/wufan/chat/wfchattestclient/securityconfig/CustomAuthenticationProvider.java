package com.wufan.chat.wfchattestclient.securityconfig;

import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @Author: wufan
 * @Date: 2019/7/5 17:09
 * 密码验证的具体逻辑
 */
@Component
public class CustomAuthenticationProvider
        implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if(password == null || password.equals("")){
            throw new BadCredentialsException("Wrong Password");
        }
//        if (shouldAuthenticateAgainstThirdPartySystem()) {
//
//            // use the credentials
//            // and authenticate against the third-party system
//            return new UsernamePasswordAuthenticationToken(
//                    name, password, new ArrayList<>());
//        } else {
//            return null;
//        }
        User user = userService.findUserByName(username);
//        return new UsernamePasswordAuthenticationToken(
//                "3", "3", new ArrayList<>());
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        if(password.equals(user.getPassword())){
            logger.info("登录信息: username:"+username+"  password:"+password);
            System.err.println(username+" 登录了！");
            return new UsernamePasswordAuthenticationToken(
                    username, password, new ArrayList<>());}
        return null;

//        return new UsernamePasswordAuthenticationToken(
//                    "3", "3", new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
