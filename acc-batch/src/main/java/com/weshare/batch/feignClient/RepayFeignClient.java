package com.weshare.batch.feignClient;

import com.weshare.service.api.client.RepayClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.feignClient
 * @date: 2021-06-10 15:24:41
 * @describe:
 */
@FeignClient(name = "${service.acc-repay-service}",contextId = "repayFeignClient",url = "${host.acc-repay-host}")
public interface RepayFeignClient extends RepayClient {
}
