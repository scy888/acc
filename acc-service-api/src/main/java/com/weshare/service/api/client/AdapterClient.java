package com.weshare.service.api.client;

import com.weshare.service.api.entity.LoanDetailReq;
import com.weshare.service.api.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.client
 * @date: 2021-05-27 10:44:12
 * @describe:
 */
@RequestMapping("/client")
public interface AdapterClient {

    @PostMapping("/saveAllLoanDetail")
    Result saveAllLoanDetail(@RequestBody List<? extends LoanDetailReq> list);
}
