package com.weshare.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch
 * @date: 2021-04-30 11:59:30
 * @describe:
 */
@SpringBootApplication
@EnableBatchProcessing
@EnableJpaAuditing//自动加载时间的
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@EnableAsync
public class BatchApplication {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(BatchApplication.class);
        long end = System.currentTimeMillis();
        System.out.println(String.format("acc-batch服务启动耗时:%d 毫秒", end - start));
    }
}

