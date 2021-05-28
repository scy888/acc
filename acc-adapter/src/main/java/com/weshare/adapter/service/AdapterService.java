package com.weshare.adapter.service;

import com.weshare.adapter.feignCilent.LoanFeignClient;
import com.weshare.service.api.client.AdapterClient;
import com.weshare.service.api.entity.LoanContractReq;
import com.weshare.service.api.entity.LoanDetailReq;
import com.weshare.service.api.entity.LoanTransFlowReq;
import com.weshare.service.api.enums.ProjectEnum;
import com.weshare.service.api.result.Result;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.service
 * @date: 2021-05-28 14:04:33
 * @describe:
 */
@Service
@Slf4j
public class AdapterService {

    @Autowired
    private LoanFeignClient loanFeignClient;


    public Result saveAllLoanContractAndLoanTransFlow(List<? extends LoanDetailReq> list, String batchDate) {

        //保存acc_loan.loan_contract
        loanFeignClient.saveAllLoanContract(
                list.stream().map(req -> {
                    LoanContractReq loanContractReq = new LoanContractReq();
                    return loanContractReq
                            .setDueBillNo(req.getDueBillNo())
                            .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                            .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                            .setProductName(ProjectEnum.getProductName(loanContractReq.getProjectNo(), loanContractReq.getProductNo()))
                            .setContractAmount(req.getLoanAmount())
                            .setInterestRate(new BigDecimal("0.02"))
                            .setTotalTerm(req.getTerm())
                            .setPrincipal(req.getLoanAmount())
                            .setBatchDate(req.getBatchDate())
                            .setRemark("放款成功");
                }).collect(Collectors.toList())
        );

        //保存acc_loan.loan_trans_flow
        loanFeignClient.saveAllLoanTransFlow(
                list.stream().filter(e -> e.getLoanStatus().equals("01")).map(req -> {
                    LoanTransFlowReq loanTransFlowReq = new LoanTransFlowReq();
                    LocalDate localDate = req.getBatchDate();
                    LocalDateTime localDateTime = LocalDateTime.now().withYear(localDate.getYear()).withMonth(localDate.getMonthValue()).withDayOfMonth(localDate.getDayOfMonth());
                    loanTransFlowReq
                            .setFlowSn(SnowFlake.getInstance().nextId() + "")
                            .setProjectNo(ProjectEnum.YXMS.getProjectNo())
                            .setProductNo(ProjectEnum.YXMS.getProducts().get(0).getProductNo())
                            .setDueBillNo(req.getDueBillNo())
                            .setTransAmount(req.getLoanAmount())
                            .setTransTime(localDateTime)
                            .setRemark("放款交易流水成功")
                            .setBatchDate(req.getBatchDate());
                    return loanTransFlowReq;
                }).collect(Collectors.toList()), batchDate
        );

        return Result.result(true);
    }
}
