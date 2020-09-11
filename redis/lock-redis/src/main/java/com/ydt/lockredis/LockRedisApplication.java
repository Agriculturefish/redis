package com.ydt.lockredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ydt.lockredis.dao")
@EntityScan(basePackages = "com.ydt.lockredis.domain")
public class LockRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LockRedisApplication.class, args);
    }

}
