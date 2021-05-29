package com.weshare.repay.provider;

import com.weshare.repay.RepayApplication;
import com.weshare.repay.entity.RepaySummary;
import com.weshare.repay.repo.RepayPlanRepo;
import com.weshare.repay.repo.RepaySummaryRepo;
import com.weshare.service.api.client.RepayClient;
import com.weshare.service.api.vo.DueBillNoAndTermDueDate;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
    @Autowired
    private RepaySummaryRepo repaySummaryRepo;
    @Autowired
    private RepayPlanRepo repayPlanRepo;

    @Test
    void count() {
        System.out.println(repaySummaryRepo.countByProjectNo("WS121212"));
        System.out.println((int)Math.ceil(17*1.0/4 ));

        repayClient.RefreshRepaySummaryCurrentTerm("WS121212","2020-05-15");
    }
}