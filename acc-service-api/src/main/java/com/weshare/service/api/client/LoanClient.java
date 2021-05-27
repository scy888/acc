package com.weshare.service.api.client;

import com.weshare.service.api.entity.LoanDetailReq;
import com.weshare.service.api.entity.User;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.client
 * @date: 2021-05-18 14:59:10
 * @describe:
 */
@RequestMapping("/client")
public interface LoanClient {

    @PostMapping("/saveListUserBase")
    Result saveListUserBase(@RequestBody List<UserBaseReq> userBaseReqList) throws Exception;

    @GetMapping("/tesGetUrl")
    Result tesGettUrl(@RequestParam("name") String name, @RequestParam("age") Integer age);

    @PostMapping("/tesPostUrl")
    Result tesPostUrl(@RequestBody User user);

    @PostMapping("/saveAllLoanContract")
    Result saveAllLoanContractAndLoanTransFlow(@RequestBody List<? extends LoanDetailReq> list);
}
