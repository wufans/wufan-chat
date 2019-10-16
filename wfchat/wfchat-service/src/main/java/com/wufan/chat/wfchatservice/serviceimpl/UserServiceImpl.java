package com.wufan.chat.wfchatservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.RedisService;
import com.wufan.chat.wfchatcontract.serviceapi.UserService;
import com.wufan.chat.wfchatrepository.mapper.FriendDao;
import com.wufan.chat.wfchatrepository.mapper.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @Author: wufan
 * @Date: 2019/7/4 20:06
 * 用户服务的实现类
 */
@Service(version = "1.0.0")
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserDao userDao;
    @Autowired
    FriendDao friendDao;
    @Autowired
    JavaMailSender javaMailSender;
//
    @Autowired
    RedisService redisService;

    @Value("${spring.mail.username}")
    private String myEmail;

    @Override
    public User findUserByName(String username) {
        //todo  基于redis缓存实现 更新方式：先从缓存中获取用户，没有则取数据表中 数据，再将数据写入缓存
        User user = redisService.getUserByName(username);
//        return userDao.getByName(username);
        return user;
    }

    /**
     * 重复返回400，不重复返回200
     * @param username
     * @return
     */
    @Override
    public int varifyName(String username) {
        //todo  基于redis缓存实现 更新方式：先从缓存中获取用户，没有则取数据表中 数据，再将数据写入缓存
        return findUserByName(username) == null? 200:400;
    }


    @Override
    public int insertUser(String username,String password,String email,Integer sex) {
        //todo 先更新数据表，成功之后，删除原来的缓存，再更新缓存

        // 随机分配一个头像
        String profile = profiledatabase[(int)(Math.random()*10)];

        User user = new User(0L,username,password,email,profile,sex);
        //重复用户名
        if(findUserByName(user.getUsername())!=null){
            return 300;
        }
//        Integer result = userDao.insertUser(user);
        Integer result = redisService.insertUser(user);
        return result != null? 200 : 400;
    }

    @Override
    public void updatePass(String username, String password) {
        Integer i = userDao.updatePwd(username, password);
    }

    @Override
    public void logOut(String username) {
    }

//    @Override
//    public Integer isFriendOrNot(String fromusername,String tousername){
//        List<FriendRequest> requests = friendDao.isFriendOrNot(fromusername, tousername);
//
//        if(requests != null && !requests.isEmpty()){
//
//            return 1;
//        }
//        return 0;
//    }

    @Override
    public void sendVarifyEmail(String toEmail, String tile, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(myEmail);
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(tile);
        simpleMailMessage.setText(content);
        javaMailSender.send(simpleMailMessage);
    }

    private static String[] profiledatabase = new String[]{
            "https://pic.qqtn.com/up/2019-6/2019061908530455124.jpg",
            "https://pic.qqtn.com/up/2019-6/2019061908530428963.jpg",
            "https://pic.qqtn.com/up/2019-6/2019061908530571123.jpg",
            "https://pic.qqtn.com/up/2019-6/2019061908530515038.jpg",
            "https://pic.qqtn.com/up/2019-3/2019030817071867046.jpg",
            "https://pic.qqtn.com/up/2019-5/2019052218242779080.jpg",
            "https://pic.qqtn.com/up/2019-5/2019052218242710046.jpg",
            "https://pic.qqtn.com/up/2019-5/2019052218242831803.jpg",
            "https://pic.qqtn.com/up/2019-5/2019052218242864250.jpg",
            "https://pic.qqtn.com/up/2019-5/2019052218242833271.jpg"
    };
}
