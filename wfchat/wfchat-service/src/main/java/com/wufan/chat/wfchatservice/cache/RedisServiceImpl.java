package com.wufan.chat.wfchatservice.cache;

import com.alibaba.dubbo.config.annotation.Service;
import com.wufan.chat.wfchatcommon.model.User;
import com.wufan.chat.wfchatcontract.serviceapi.RedisService;
import com.wufan.chat.wfchatrepository.mapper.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


import java.util.concurrent.TimeUnit;

/**
 * @Author: wufan
 * @Date: 2019/7/10 19:08
 */
@Service(version = "1.0.0")
public class RedisServiceImpl implements RedisService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

    //获取user
    @Override
    public User getUserByName(String username) {
        String key = "user_" + username;

        ValueOperations<String, User> operations = redisTemplate.opsForValue();

        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            User user = operations.get(key);
            System.err.println("在redis缓存中查找了用户 "+username);
            return user;
        } else {
            User user = userDao.getByName(username);
            // 写入缓存
            if(user != null){
                operations.set(key, user, 5, TimeUnit.HOURS);
            }
            return user;
        }
    }
    //修改user
    @Override
    public int deleteUser(String username){
        String key = "user_" + username;
        //更新缓存
        if(redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
            System.err.println("在redis缓存中删除了用户 "+username);
        }
        Integer result = userDao.deleteUser(username);
        return result == null ? 400 : 200;
        //更新数据库
    }


    //插入user
    @Override
    public int insertUser(User user){
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        Integer result = userDao.insertUser(user);
        if (result != null) {
            String key = "user_" + user.getUsername();
            boolean haskey = redisTemplate.hasKey(key);
            if (haskey) {
                redisTemplate.delete(key);
            }
            // 再将更新后的数据加入缓存
            operations.set(key, user, 3, TimeUnit.HOURS);
            System.err.println("在redis缓存中新建了用户 "+user.getUsername());
        }
        return result;

    }


}
