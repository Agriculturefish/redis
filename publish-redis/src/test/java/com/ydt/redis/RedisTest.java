package com.ydt.redis;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisTest {


    Jedis jedis;
    ApplicationContext context;
    @Before
    public void init() {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        JedisPool jedisPool = (JedisPool) context.getBean("jedisPool");
        jedis = jedisPool.getResource();
    }

    /**
     * 简单String类型
     */
    @Test
    public void insertString() {
        jedis.del("length");
        jedis.set("length", "19");
        jedis.setnx("length", "19");//如果数据库已经有了，那么它就不能再次插入
        System.out.println(jedis.get("length"));
    }

    /**
     * hash类型一般应用于保存对象
     */
    @Test
    public void insertHash() {
        jedis.hset("user", "name", "laohu");
        jedis.hset("user", "age", "18");
        System.out.println(jedis.hget("user", "name"));
        System.out.println(jedis.hgetAll("user"));
    }

    /**
     * List
     */
    @Test
    public void insertList() {
        jedis.del("person");
        jedis.del("city");
        jedis.lpush("person", "laowang", "laoli", "laohe");//左压栈
        jedis.rpush("city", "changsha", "shenzhen", "guangzhou");//右压栈

        /*System.out.println(jedis.lpop("person"));
        System.out.println(jedis.lpop("city"));*/

        System.out.println(jedis.lrange("person", 0, -1));
    }

    /**
     * Set (无序)
     */
    @Test
    public void insertSet() {
        jedis.sadd("username", "laozhang", "laoxie", "laojiang");
        jedis.sadd("password", "laohu", "laoxie", "laojiang");
        System.out.println(jedis.smembers("username"));
        System.out.println(jedis.sdiff("password", "username"));//取差值
    }

    /**
     * 有序Set
     */
    @Test
    public void insertZSet() {
        jedis.zadd("tuhao", 1000, "mayun");
        jedis.zadd("tuhao", 1, "wangjianlin");
        jedis.zadd("tuhao", 100, "mahuateng");
        System.out.println(jedis.zrange("tuhao", 0, -1));//屌丝榜
        System.out.println(jedis.zrevrange("tuhao", 0, -1));//土豪榜
    }

    /**
     * 演示String和批量操作
     */
    @Test
    public void insertPipeLine(){
        Logger logger = Logger.getLogger(RedisTest.class);
        Pipeline pipe = jedis.pipelined(); // 先创建一个 pipeline 的链接对象（管道，替换了jedis的单步操作）
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipe.set(String.valueOf(i), String.valueOf(i));
        }
        pipe.sync();
        long end = System.currentTimeMillis();
        logger.info("the general total time is:" + (end - start));

        String v = "aaasssssssssssssssssdakldwndonqwdknaknwndddddooooo" +
                "addasssssssssssssssssssssssssssaaasssssssssssssssssdak" +
                "ldwndonqwdknaknwndddddoooooaddassssssssssssssssssssssss" +
                "sssaaasssssssssssssssssdakldwndonqwdknaknwndddddoooooadd" +
                "asssssssssssssssssssssssssssaaasssssssssssssssssdakldwn" +
                "donqwdknaknwndddddoooooaddasssssssssssssssssssssssssssaaas" +
                "ssssssssssssssssdakldwndonqwdknaknwndddddoooooaddasssssssssss" +
                "ssssssssssssssssaaasssssssssssssssssdakldwndonqwdknaknwndddddooooo" +
                "addasssssssssssssssssssssssssssaaasssssssssssssssssdakldwndonqwdknak" +
                "nwndddddoooooaddasssssssssssssssssssssssssssaaasssssssssssssssssdakldwndo" +
                "nqwdknaknwndddddoooooaddasssssssssssssssssssssssssssaaasssssssssssssssssdak" +
                "ldwndonqwdknaknwndddddoooooa";

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipe.set(String.valueOf(i), v + String.valueOf(i));
        }
        pipe.sync();
        end = System.currentTimeMillis();
        logger.info("the general total time is:" + (end - start));
    }

    @Test
    public void testGeneralAndPipeline(){
        Logger logger = Logger.getLogger(RedisTest.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.set(String.valueOf(i), String.valueOf(i));
        }
        long end = System.currentTimeMillis();
        logger.info("the general total time is:" + (end - start));

        Pipeline pipe = jedis.pipelined(); // 先创建一个 pipeline 的链接对象
        long start_pipe = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            pipe.set(String.valueOf(i), String.valueOf(i));
        }
        pipe.sync(); // 获取所有的 response
        long end_pipe = System.currentTimeMillis();
        logger.info("the pipe total time is:" + (end_pipe - start_pipe));
    }

    @Test
    public void testMulti() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            if( i == 0 ){
                jedis.set("" + i, i +"");
                Thread.sleep(10000);//捣乱将数据改掉
            }else {
                jedis.set("" + i, (i + Integer.valueOf(jedis.get((i-1)+""))) + "");
            }
        }
    }

    @Test
    public void testTransaction() throws InterruptedException {
        Transaction transaction = jedis.multi();
        int last = 0;
        for (int i = 0; i < 10; i++) {
            if( i == 0 ){
                transaction.set("" + i, i +"");
                Thread.sleep(10000);
            }else {
                transaction.set("" + i, (i + last) + "");
            }
            last = i +last;
        }
        transaction.exec();
    }

    @Test
    public void testWatch() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            executorService.execute(new Runnable() {
                public void run() {
                    go();
                }
            });
        }

    }

    private void go() {
        JedisPool jedisPool = (JedisPool) context.getBean("jedisPool");
        Jedis jedis = jedisPool.getResource();
        try {
            String key_s = "user_name";// 抢到的用户
            String key = "test_count";// 商品数量
            String clientName = UUID.randomUUID().toString().replace("-", "");// 用户名字
            while (true) {
                try {
                    jedis.watch(key);// key加上乐观锁
                    System.out.println("用户:" + clientName + "开始抢商品");
                    System.out.println("当前商品的个数：" + jedis.get(key));
                    int prdNum = Integer.parseInt(jedis.get(key));// 当前商品个数
                    if (prdNum > 0) {
                        Transaction transaction = jedis.multi();// 标记一个事务块的开始
                        transaction.set(key, String.valueOf(prdNum - 1));
                        List<Object> result = transaction.exec();// 原子性提交事物
                        if (result == null || result.isEmpty()) {
                            System.out.println("用户:" + clientName + "没有抢到商品");// 可能是watch-key被外部修改，或者是数据操作被驳回
                        } else {
                            jedis.sadd(key_s, clientName);// 将抢到的用户存起来
                            System.out.println("用户:" + clientName + "抢到商品");
                            break;
                        }
                    } else {
                        System.out.println("库存为0，用户:" + clientName + "没有抢到商品");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    jedis.unwatch();// exec，discard，unwatch命令都会清除连接中的所有监视
                }
            } // while
        } catch (Exception e) {
            System.out.println("redis bug:" + e.getMessage());
        } finally {
            // 释放jedis连接
            try {
                jedis.close();
            } catch (Exception e) {
                System.out.println("redis bug:" + e.getMessage());

            }
        }
    }

}
