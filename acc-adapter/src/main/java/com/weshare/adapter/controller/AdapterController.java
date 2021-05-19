package com.weshare.adapter.controller;

import com.weshare.adapter.feignCilent.LoanFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.controller
 * @date: 2021-05-16 18:37:47
 * @describe:
 */
@RestController
@Slf4j
public class AdapterController {

    @Autowired
    private LoanFeignClient loanFeignClient;

}
