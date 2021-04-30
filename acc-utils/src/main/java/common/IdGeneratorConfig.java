package common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

@Slf4j
@Configuration
public class IdGeneratorConfig {

    @Bean
    public SnowFlake snowFlake() {
        String ip;
        int dataCenter = 1, machineId = 1;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            log.info("本机IP是：{}", ip);
            if (ip != null && ip.split("\\.").length == 4) {
                String dataCenterStr = ip.split("\\.")[2];
                String machineIdStr = ip.split("\\.")[3];
                dataCenter = Integer.parseInt(dataCenterStr);
                machineId = Integer.parseInt(machineIdStr);
            } else {
                // IP地址不带点，没有获得IPv4的地址
                dataCenter = new Random().nextInt(255);
                machineId = new Random().nextInt(255);
            }
        } catch (UnknownHostException e) {
            // 如果获取异常，则从0-255之间随机获取一个数做为机器号
            dataCenter = new Random().nextInt(255);
            machineId = new Random().nextInt(255);
        }
        log.info("本服务dataCenter：{}，machineId：{}", dataCenter % 32, machineId % 32);
        // 数据中心最大为31，机器号最大为31
        return new SnowFlake(dataCenter % 32, machineId % 32);
    }

}
