package com.weshare.loan.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weshare.loan.entity.UserBase;
import com.weshare.service.api.client.LoanClient;
import com.weshare.service.api.entity.UserBaseReq;
import com.weshare.service.api.result.Result;
import common.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.loan.dao
 * @date: 2021-05-18 17:27:42
 * @describe:
 */
@SpringBootTest
class LoanDaoTest {
    @Autowired
    private LoanDao loanDao;
    @Autowired
    private LoanClient loanClient;

    @Test
    public void jdbcTest() throws Exception {

        List<String> readAllLines = Files.readAllLines(Paths.get("/incomeApply", "userbase.json"));
        List<UserBaseReq> baseReqList = JsonUtil.fromJson(String.join(System.lineSeparator(), readAllLines), new TypeReference<List<UserBaseReq>>() {
        });
        List<String> dueBillNoList = baseReqList.stream().map(UserBaseReq::getDueBillNo)
                .collect(Collectors.toList());
        loanDao.deleteUserBaseByDueBillNoList(dueBillNoList);
        loanDao.addUserBaseList(baseReqList);
        List<UserBase> userBaseList = loanDao.findUserBaseByDueBillNo(dueBillNoList);
        System.out.println(JsonUtil.toJson(userBaseList, true));
        loanDao.updateUserBaseList(baseReqList);
    }

    @Test
    public void saveListUserBaseTest() throws Exception {
        List<String> readAllLines = Files.readAllLines(Paths.get("/incomeApply", "userbase.json"));
        List<UserBaseReq> baseReqList = JsonUtil.fromJson(String.join(System.lineSeparator(), readAllLines), new TypeReference<List<UserBaseReq>>() {
        });
        Result result = loanClient.saveListUserBase(baseReqList);
        System.out.println(JsonUtil.toJson(result, true));
    }

    @Test
    public void currentTerm() {
        loanClient.UpdateRepaySummaryCurrentTerm("WS121212", "2020-05-15");
    }

    @Test
    public void testUpdateStatus() {
        loanClient.UpdateLoanContractStatus(
                List.of(new LoanClient.UpdateLoanContractStatus()
                        .setBatchDate("2021-05-30")
                        .setDueBillNo("YX-101"))
        );
    }
}