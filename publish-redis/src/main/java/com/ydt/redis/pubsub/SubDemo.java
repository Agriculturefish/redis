package com.ydt.redis.pubsub;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SubDemo {

    public static void main( String[] args )
    {
        // 连接redis服务端
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
        
        System.out.println(String.format("redis pool is starting, redis ip %s, redis port %d", "127.0.0.1", 6379));

        SubThread subThread1 = new SubThread(jedisPool);  //订阅者1
        subThread1.start();

        SubThread subThread2 = new SubThread(jedisPool);  //订阅者2
        subThread2.start();
    }
}