package com.weshare.eureka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.eureka
 * @date: 2021-04-25 23:06:22
 * @describe:
 */

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SpringApplication.run(EurekaApplication.class);
        long end = System.currentTimeMillis();
        System.out.println(String.format("注册中心服务启动耗时:%d 毫秒",end-start));
    }
}
