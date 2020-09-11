package com.ydt.redis.pubsub;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class PubDemo {

    public static void main( String[] args )
    {
        // 杩炴帴redis鏈嶅姟绔�
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
        
        System.out.println(String.format("redis pool is starting, redis ip %s, redis port %d", "127.0.0.1", 6379));

        Publisher publisher = new Publisher(jedisPool);    //鍙戝竷鑰�
        publisher.start();
    }
}