package com.ydt.lockredis.util;

import com.ydt.lockredis.config.RedissonConfig;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonLock {
	private Config config;
	private RedissonClient client;
	@Autowired
	private RedissonConfig redissonConfig;
	@PostConstruct
	public void init() {
		if(null == client) {
			config = redissonConfig.getConfig();
			client = Redisson.create(config);
			System.out.println("====================redisson created");
		}
	}
	
	public RedissonClient getClient() {
		config = redissonConfig.getConfig();
		RedissonClient c = Redisson.create(config);
		return c;
	}
	

	public boolean release(String code) {
		RLock lock = client.getLock(code);
		if(null != lock) {
			lock.unlock();
			return true;
		}else {
			System.out.println("=============没有找到锁");
			return false;
		}
	}

	public boolean lock(String code){
		RLock lock = client.getLock(code);
		try {
			//第一个参数表示不等待，娶不到锁就拉倒
			//第二个是设置锁过期时间
			return lock.tryLock(0,5,TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
}