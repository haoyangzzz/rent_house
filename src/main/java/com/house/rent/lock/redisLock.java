package com.house.rent.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class redisLock {
    public static final long TIME_OUT = 10;
    @Autowired
    private StringRedisTemplate template;

    public boolean lock(String key,String value){
        //去得到值说明有锁
        if (template.hasKey(key)) {
            return false;
        }else {
            template.opsForValue().set(key, value, TIME_OUT, TimeUnit.SECONDS);
            return true;
        }
    }
    public void unlock(String key,String value){
        if (template.opsForValue().get(key).equals(value)){
            template.delete(key);
        }
    }
}
