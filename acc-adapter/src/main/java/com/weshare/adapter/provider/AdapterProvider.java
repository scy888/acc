package com.weshare.adapter.provider;

import com.weshare.adapter.entity.LoanDetail;
import com.weshare.adapter.repo.LoanDetailRepo;
import com.weshare.adapter.service.AdapterService;
import com.weshare.service.api.client.AdapterClient;
import com.weshare.service.api.entity.LoanDetailReq;
import com.weshare.service.api.entity.RepaymentPlanReq;
import com.weshare.service.api.result.Result;
import common.ChangeEnumUtils;
import common.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.adapter.provider
 * @date: 2021-05-27 10:42:56
 * @describe:
 */
@RestController
@Slf4j
public class AdapterProvider implements AdapterClient {

    @Autowired
    private LoanDetailRepo loanDetailRepo;
    @Autowired
    private AdapterService adapterService;

    @Transactional
    @Override
    @Async
    public Result saveAllLoanDetail(List<? extends LoanDetailReq> list) {
        log.info("saveAllLoanDetail()方法的异步调用的线程名:{}", Thread.currentThread().getName());
        loanDetailRepo.deleteByDueBillNoList(list.stream().map(LoanDetailReq::getDueBillNo).collect(Collectors.toList()));
        loanDetailRepo.saveAll(
                list.stream().map(e -> {
                    LoanDetail loanDetail = new LoanDetail();
                    BeanUtils.copyProperties(e, loanDetail);
                    loanDetail.setId(SnowFlake.getInstance().nextId() + "")
                            .setLoanStatus(ChangeEnumUtils.changeEnum("WS121212", "loanStatus", e.getLoanStatus(), LoanDetail.LoanStatusEnum.class))
                            .setCreatedDate(LocalDateTime.now().withYear(((LoanDetailReq) e).getBatchDate().getYear()).withMonth(((LoanDetailReq) e).getBatchDate().getMonthValue()).withDayOfMonth(((LoanDetailReq) e).getBatchDate().getDayOfMonth()))
                            .setLastModifiedDate(loanDetail.getCreatedDate());
                    return loanDetail;
                }).collect(Collectors.toSet())
        );
        //int a = 2 / 0;
        return Result.result(true);
    }

    @Override
    public Result saveAllRepaymentPlan(List<? extends RepaymentPlanReq> list) {
        return null;
    }

    @Override
    public void saveAllLoanContractAndLoanTransFlow(List<? extends LoanDetailReq> list, String batchDate) {
        adapterService.saveAllLoanContractAndLoanTransFlow(list, batchDate);
    }
}
