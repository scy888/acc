package com.weshare.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter
 * @date: 2021-05-16 16:14:51
 * @describe:
 */
@SpringBootApplication
@EnableJpaAuditing//自动加载时间的
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
public class AdapterApplication {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(AdapterApplication.class);
        long end = System.currentTimeMillis();
        System.out.println(String.format("acc-adapter服务启动耗时:%d 毫秒", end - start));

    }
}
