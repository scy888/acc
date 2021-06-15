package com.weshare.batch.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.config
 * @date: 2021-05-08 16:48:08
 * @describe:
 */

@Configuration
@Data
public class AppConfig {
    @Value("${yxms.create}")
    private String create;
    @Value("${yxms.zip}")
    private String zip;
    @Value("${yxms.unzip}")
    private String unzip;
}
