package com.ydt.lockredis.config;
 
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private int port;
	@Value("${spring.redis.timeout}")
	private int timeout;

	private static final String REDIS_PROTOCOL = "redis://";
	
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public int getTimeout() {
		return timeout;
	}


	/**
	 * 获取redisson配置类
	 * */
	public Config getConfig() {
		Config config = new Config();
		config.useSingleServer().setAddress(REDIS_PROTOCOL + host + ":" + port)
		.setDatabase(1)
		.setConnectTimeout(timeout);
		System.out.println("================config:" + host + "," + port);
		return config;
	}
}