package com.entrepreneur.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 一站式创业者综合服务平台 - 启动类
 */
@SpringBootApplication
@MapperScan("com.entrepreneur.platform.mapper")
@EnableScheduling
public class EntrepreneurPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntrepreneurPlatformApplication.class, args);
    }
}
