package com.wufan.chat.wfchattestclient.servercontroller;

import com.wufan.chat.wfchatcommon.model.DetailUser;
import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcommon.rest.RestResponse;
import com.wufan.chat.wfchatcontract.serviceapi.FriendService;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Random;

/**
 * @Author: wufan
 * @Date: 2019/7/4 19:24
 */

@RestController
@RequestMapping("/account")
public class UserServerController {
    private static final Logger logger = LoggerFactory.getLogger(UserServerController.class);

    @Autowired
    UserService userService;
    @Autowired
    FriendService friendService;

    //名称验证
    @ApiOperation(value = "注册时名称验证")
    @RequestMapping(value = "/varifyname",method = RequestMethod.POST)
    public RestResponse<String> varifyName(String username){
        logger.error("varify name:"+username);
        Integer code = userService.varifyName(username);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(code).setMsg("验证完成");
        if(username.length()>10 || username.length()<2){
            restResponse.setCode(400).setMsg("用户名长度不符合规定！");
        }
        restResponse.setData("username:"+username);
        return restResponse;
    }

    //用户登录并获取自己的用户信息
    @ApiOperation(value = "用户登录获取个人信息")
    @RequestMapping(value = "/findbyname",method = RequestMethod.GET)
    public RestResponse<DetailUser> getUserInfo(Principal principal){

        RestResponse<DetailUser> restResponse = new RestResponse<>();
        String username = principal.getName();
        User user= userService.findUserByName(username);
        DetailUser detailUser = new DetailUser(user);
        restResponse.setData(detailUser);
//        System.err.println(username + " 登陆了！");
        return restResponse;
    }

    //查找用户
    @ApiOperation(value = "用户查询")
    @RequestMapping(value = "/findbyname",method = RequestMethod.POST)
    public RestResponse<DetailUser> findByName(Principal principal, String username){

        RestResponse<DetailUser> restResponse = new RestResponse<>();

        if(username == null){
            restResponse.setCode(400).setMsg("please input username!");
            return restResponse;
        }
        User user= userService.findUserByName(username);


        if(user == null){
            restResponse.setCode(400).setMsg("not exist username!");
            return restResponse;
        }
        DetailUser detailUser = new DetailUser(user);
        // 判断两个用户是否是好友
        String myusername = principal.getName();

        logger.info(myusername+"查找了用户："+username);

        detailUser.setIsfriend(friendService.isFriendOrNot(myusername,username));
        restResponse.setData(detailUser);
        return restResponse;
    }

    //注册
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register",method = RequestMethod.POST,  produces = "application/json; charset=utf-8")
    public RestResponse<String> register(String username,String password, String email, Integer sex){
        logger.info("register info:  username"+username+"--password"+password+"--email"+email+"--sex"+sex);

        int result = userService.insertUser(username, password, email, sex);

        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(result);
        if(result == 200){
            restResponse.setMsg("注册成功！");
        }else if(result == 300){
            restResponse.setMsg("重复的用户名！");
        }else{
            restResponse.setMsg("注册失败！");
        }
        return restResponse;
    }

    //修改密码
    @ApiOperation(value = "密码修改")
    @RequestMapping(value = "changepwd", method = RequestMethod.POST)
    public RestResponse<String> updatePwd(String username, String password){
        RestResponse<String> restResponse = new RestResponse<>();
        userService.updatePass(username, password);
        restResponse.setMsg("change password success!");
        logger.info(username+"update password to "+password);
        return restResponse;
    }

    //todo 邮箱验证功能, 待联调
    @ApiOperation(value = "邮箱验证")
    @RequestMapping(value = "getCheckCode",method = RequestMethod.POST)
    @ResponseBody
    public String getCheckCode(String email){
        String checkCode = String.valueOf(new Random().nextInt(899999) + 100000);
        String message = "您的注册验证码为："+checkCode;
        try {
             userService.sendVarifyEmail(email, "注册验证码", message);
        }catch (Exception e){
            return "发送成功";
        }
        return checkCode;
    }



    /**
     *
     * @param status
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/return/{status}",method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public RestResponse<String> logSuccess(@PathVariable String status){
        if("success".equals(status)){
            return new RestResponse<String>(200,"登录成功！",null);
        }
        if("failure".equals(status)){
            return new RestResponse<String>(400,"登录失败！",null);
        }
        if("logout".equals(status)){
            return new RestResponse<String>(200,"注销成功！",null);
        }
        return new RestResponse<String>(400,"请求错误！",null);
    }

}
