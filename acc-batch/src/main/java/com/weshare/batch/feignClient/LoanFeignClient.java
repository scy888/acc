package com.weshare.batch.feignClient;

import com.weshare.service.api.client.LoanClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.feignClient
 * @date: 2021-05-27 17:17:59
 * @describe:
 */
@FeignClient(name = "${service.acc-loan-service}",contextId = "loanFeignClient",url = "${host.acc-loan-host}")
public interface LoanFeignClient extends LoanClient {
}
