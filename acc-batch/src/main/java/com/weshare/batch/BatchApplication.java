package com.weshare.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch
 * @date: 2021-04-30 11:59:30
 * @describe:
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
public class BatchApplication {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(BatchApplication.class);
        long end = System.currentTimeMillis();
        System.out.println(String.format("batch服务启动耗时:%d 毫秒",end-start));
    }
}

