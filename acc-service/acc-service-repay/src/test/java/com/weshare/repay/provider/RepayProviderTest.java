package com.weshare.repay.provider;

import com.weshare.service.api.client.RepayClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.provider
 * @date: 2021-05-29 02:42:35
 * @describe:
 */
@SpringBootTest
class RepayProviderTest {
    @Autowired
    private RepayClient repayClient;

    @Test
    void saveRepayPlan() {

    }
}