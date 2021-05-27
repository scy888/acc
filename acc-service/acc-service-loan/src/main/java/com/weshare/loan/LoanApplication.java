package com.weshare.loan;

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
 * @package: com.weshare.loan
 * @date: 2021-04-26 20:13:46
 * @describe:
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableJpaAuditing//自动加载时间的
@EnableAsync
@EnableFeignClients(basePackages = "com.weshare")
public class LoanApplication {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(LoanApplication.class);
        long end = System.currentTimeMillis();
        System.out.println(String.format("acc-loan服务启动耗时:%d 毫秒",end-start));
    }
}
