package com.weshare.batch.feignClient;

import com.weshare.service.api.client.AdapterClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.batch.feignClient
 * @date: 2021-05-27 11:35:54
 * @describe:
 */
@FeignClient(name = "${service.acc-adapter-service}",contextId = "adapterFeignClient",url = "${host.acc-adapter-host}")
public interface AdapterFeignClient extends AdapterClient {
}
