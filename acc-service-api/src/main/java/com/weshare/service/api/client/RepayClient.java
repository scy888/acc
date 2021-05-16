package com.weshare.service.api.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.client
 * @date: 2021-04-26 21:54:57
 * @describe:
 */

@RequestMapping("/client")
public interface RepayClient {

    @GetMapping("/getRepayClient/{repayClient}/{isInvoking}")
    String getRepayClient(@PathVariable("repayClient") String repayClient,
                          @PathVariable("isInvoking") Boolean isInvoking);
}
