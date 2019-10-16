package com.wufan.chat.wfchatrepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WfchatRepositoryApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() {
        stringRedisTemplate.opsForValue().set("k1","v1");
        System.out.println(stringRedisTemplate.opsForValue().get("k1"));
    }


}
