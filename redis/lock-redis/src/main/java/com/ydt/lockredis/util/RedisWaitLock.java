package com.ydt.lockredis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class RedisWaitLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisWaitLock.class);

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
     * @return
     */
    public boolean lock(String code,String time) {
        try {
            do {
                // 对应setnx命令，可以成功设置,也就是key不存在，获得锁成功
                if (redisTemplate.opsForValue().setIfAbsent(code, time)) {
                    //添加超时时间，防止业务逻辑中锁释放失败，导致死锁
                    redisTemplate.expire(code, 5, TimeUnit.SECONDS);
                    return true;
                }
            } while (true);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 释放锁
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


    /**
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
     *
     * @param timeout
     * @param unit
     * @return
     */
    public boolean tryBatchLock(long timeout, TimeUnit unit) {
        try {
            List<String> needLocking = new CopyOnWriteArrayList<String>();
            List<String> locked = new CopyOnWriteArrayList<String>();
            long nano = System.nanoTime();
            do {
                redisTemplate.executePipelined(new RedisCallback<Object>() {
                    public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                        redisConnection.openPipeline();
                        return null;
                    }
                });
            } while ((System.nanoTime() - nano) < unit.toNanos(timeout));

            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            /*returnResource(jedis);*/
        }
        return true;
    }
}
 

