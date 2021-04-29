package com.weshare.repay.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.controller
 * @date: 2021-04-26 21:46:38
 * @describe:
 */
@RestController
@Slf4j
public class RepayController {
    @GetMapping("/getRepay/{clientName}")
    public String getRepay(@PathVariable String clientName) {
        clientName = "还款服务的服务名是: " + clientName;
        log.info(clientName);
        return clientName;
    }
}
