package com.weshare.adapter.feignCilent;

import com.weshare.service.api.client.LoanClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.feignCilent
 * @date: 2021-05-19 14:16:03
 * @describe:
 */
@FeignClient(name = "${service.acc-loan-service}",contextId = "loanFeignClient",url = "${host.acc-loan-host}")
public interface LoanFeignClient extends LoanClient {
}
