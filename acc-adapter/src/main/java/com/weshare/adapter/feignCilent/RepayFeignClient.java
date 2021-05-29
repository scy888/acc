package com.weshare.adapter.feignCilent;

import com.weshare.service.api.client.RepayClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.feignCilent
 * @date: 2021-05-29 02:55:50
 * @describe:
 */
@FeignClient(name = "${service.acc-repay-service}",contextId = "repayFeignClient",url = "${host.acc-repay-host}")
public interface RepayFeignClient extends RepayClient {
}
