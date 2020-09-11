package com.ydt.lockredis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
public class RedisNoWaitLock {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 加锁
     *
     * @param lockKey   加锁的Key
     * @param timeStamp 时间戳：当前时间+超时时间
     * @return
     */
    public boolean lock(String lockKey, String timeStamp) {
        // 对应setnx命令，可以成功设置,也就是key不存在，获得锁成功
        // Jedis  ---》 setNx
        if (redisTemplate.opsForValue().setIfAbsent(lockKey, timeStamp)) {
            //添加超时时间，防止业务逻辑中锁释放失败，导致死锁
            redisTemplate.expire(lockKey, 5, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public void release(String lockKey,String timeStamp) {
        try {
            String redisTimeStamp = (String) redisTemplate.opsForValue().get(lockKey);
            //高并发场景下，秒杀时间太久，锁永久失效问题,判断是否为当前线程，如果是那就删除
            if(redisTimeStamp != null && timeStamp != null &&timeStamp.equals(redisTimeStamp)){
                // 删除锁状态
                redisTemplate.opsForValue().getOperations().delete(lockKey);
            }
        } catch (Exception e) {
            System.out.println("警报！警报！警报！解锁异常");
        }
    }
}
