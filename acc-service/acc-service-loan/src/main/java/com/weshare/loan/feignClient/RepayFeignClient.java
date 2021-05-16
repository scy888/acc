package com.weshare.loan.feignClient;

import com.weshare.service.api.client.RepayClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.feignClient
 * @date: 2021-04-26 22:17:01
 * @describe:
 */
//*${service.acc-repay-service},*/
@FeignClient(name = "${service.acc-repay-service}", contextId = "repayFeignClient", url = "${host.acc-repay-host}")
public interface RepayFeignClient extends RepayClient {
}
