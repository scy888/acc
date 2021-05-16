package com.weshare.loan.controller;

import com.weshare.service.api.client.RepayClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.controller
 * @date: 2021-04-26 21:33:47
 * @describe:
 */
@RestController
@Slf4j
public class LoanController {
    @Autowired
    private RepayClient repayClient;

    @GetMapping("/getLoan/{clientName}")
    public String getLoan(@PathVariable String clientName) {
        clientName = " 放款服务的服务名是: " + clientName;
        log.info(clientName);
        return clientName;
    }

    @GetMapping("/getRepayFeignClient/{clientName}/{isInvoking}")
    public String getRepayFeignClient(@PathVariable String clientName,
                                      @PathVariable Boolean isInvoking) {

        clientName = this.repayClient.getRepayClient(clientName, isInvoking);
        log.info(clientName);
        return clientName;
    }
}
